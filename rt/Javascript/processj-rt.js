__RT = function() {
    //////
    // Scheduler logic
    //////
    
    var processes = {};
    
    var runQueue = [];
    
    var schedule = function(p) {
	runQueue.push(p);
    }
    
    var main = function() {
	while (runQueue.length !== 0) {
	    p = runQueue.pop();
	    if (p.nextSlice !== -1)
	    {
		p.slices[p.nextSlice]();
	    }
	}
    }
    
    var ChannelState = { idle : 0, awaitingRead : 1, awaitingWrite : 2 }
    
    //////
    // Process
    //////
    
    function Process(name) {
	this.name = name;
	this.commData = null;
	this.completionBarrier = null;
	this.nextSlice = 0;
	// Register process
	processes[name] = this;
    }
    
    Process.prototype.start = function() {
	schedule(this);
    }
    
    Process.prototype.finish = function() {
	console.log("--finish() called for process " + this.name);
	// Unregister process
	delete processes[this.name];
	// Synchronize on completion barrier
	if (this.completionBarrier !== null) {
	    this.completionBarrier.sync(this);
	}
	this.nextSlice = -1;
    }
    
    Process.prototype.toString = function() {
	return this.name;
    }
    
    Process.prototype.enrollOnCompletionBarrier = function(cb) {
	cb.enroll(this);
	this.completionBarrier = cb;
    }
    
    function defineProcess(processName, createProcSlicesWithNewContext) {
	function P() {
	    Process.call(this, processName + "$" + (P.prototype.instanceCounter++));
	    this.slices = createProcSlicesWithNewContext(this);
	}
	P.prototype = new Process();
	P.prototype.constructor = P;
	P.prototype.instanceCounter = 0;
	return P;
    }
    
    //////
    // Channel
    //////
    
    function Channel() {
	this.data = null;
	this.state = ChannelState.idle;
	this.waitingProcess = null;
    }
    
    Channel.prototype.write = function(p) {
	if (this.state == ChannelState.awaitingWrite) {
	    this.waitingProcess.commData = p.commData;
	    p.commData = null;
	    schedule(this.waitingProcess);
	    this.waitingProcess = null;
	    schedule(p);
	    this.state = ChannelState.idle;
	}
	else {
	    this.waitingProcess = p;
	    this.state = ChannelState.awaitingRead;
	}
    }
    
    Channel.prototype.read = function(p) {
	if (this.state == ChannelState.awaitingRead) {
	    p.commData = this.waitingProcess.commData;
	    this.waitingProcess.commData = null;
	    schedule(this.waitingProcess);
	    this.waitingProcess = null;
	    schedule(p);
	    this.state = ChannelState.idle;
	}
	else {
	    this.waitingProcess = p;
	    this.state = ChannelState.awaitingWrite;
	}
    }
    
    //////
    // Barrier
    /////

    function Barrier() {
	this.enrolledProcs = {};
	this.nEnrolled = 0;
	this.waitingProcs = {};
	this.nWaiting = 0;
    }
    
    Barrier.prototype.enroll = function(p){
	this.enrolledProcs[p] = true;
	this.nEnrolled++;
    }
    
    Barrier.prototype.resign = function(p) {
	if (p in this.enrolledProcs) {
	    delete this.enrolledProcs[p];
	    this.nEnrolled--;
	}
    }

    Barrier.prototype.sync = function(p) {
	// If process is not enolled, this is an error.
	//console.log(this.enrolledProcs);
	//console.log(p.toString() in Object.keys(this.enrolledProcs));
	if (!(p in this.enrolledProcs)) {
	    return;
	}

	// Add process to wait list
	if (!(p in this.waitingProcs)) {
	    this.waitingProcs[p] = true;
	    this.nWaiting++;
	}
	
	// Check if barrier is completed
	if (this.nEnrolled === this.nWaiting) {
	    console.log("barrier completed");
	    // Allow all processes to continue
	    for (var pname in this.enrolledProcs) {
		//console.log(pname);
		schedule(processes[pname]);
	    }
	    // Clear wait list
	    this.waitingProcs = {};
	    this.nWaiting = 0;
	}
    }
    
    //////
    // Completion barrier
    /////
    function CompletionBarrier(resumeProc) {
	this.nEnrolled = 0;
	this.nCompleted = 0;
	this.resumeProc = resumeProc;
	this.enrolledProcs = {};
    }
    
    CompletionBarrier.prototype.enroll = function(p) {
	this.nEnrolled++;
	this.enrolledProcs[p.name] = true;
    }
    
    CompletionBarrier.prototype.sync = function(p) {
	if (!(p in this.enrolledProcs)) {
	    return;
	}
	
	console.log("CompletionBarrier: process " + p.name + "completed");
	
	this.nCompleted++;
	
	if (this.nEnrolled == this.nCompleted && this.resumeProc !== null) {
	    schedule(this.resumeProc);
	}
    }
    
    //////
    // Functions
    //////

    // runInParallel
    // First argument should be a process to resume after finishing the PAR.
    // The rest of the arguments 
    function runInParallel(resumeProc, processes) {	
	var cb = new CompletionBarrier(resumeProc);
	
	// For each process in the argument list,
	// set the completion barrier.
	for (var i = 0; i < processes.length; i++) {
	    var p = processes[i];
	    p.enrollOnCompletionBarrier(cb);
	}
	
	// Start the processes.
	for (var i = 0; i < processes.length; i++) {
	    var p = processes[i];
	    console.log("PAR: starting " + p.name);
	    p.start();
	}
    }
    
    //////
    // Public interface
    //////
    
    return {
	helloWorld : function() { console.log("Hello, world."); },
	defineProcess : defineProcess,
	Channel : Channel,
	Barrier : Barrier,
	runInParallel : runInParallel,
	main : main
    };
}();

var c1 = new __RT.Channel();
var b1 = new __RT.Barrier();

////// Test process Recv, receives a value on c1
var Recv = __RT.defineProcess("Recv", function(self) {
    var a = 0;
    var b = 0;
    return [
	function() {
	    b1.enroll(self);
	    a += 1;
	    c1.read(self);
	    self.nextSlice = 1;
	},
	function() {
	    a = self.commData;
	    b = a * 2;
	    b1.sync(self);
	    self.nextSlice = 2;
	},
	function() {
	    console.log(a);
	    console.log(b);
	    self.finish();
	}
    ];
});
//////

////// Test process Send, sends a value on c1
var Send = __RT.defineProcess("Send", function(self) {
    return [
	function() {
	    b1.enroll(self);
	    self.commData = 999;
	    c1.write(self);
	    self.nextSlice = 1;
	},
	function() {
	    b1.sync(self);
	    self.nextSlice = 2;
	},
	function() {
	    self.finish();
	}
    ]
});
//////

////// Test process Main, runs Send and Recv in parallel
var Main = __RT.defineProcess("Main", function(self) {
    var S = null;
    var R = null;
    return [
	function() {
	    S = new Send();
	    R = new Recv();
	    __RT.runInParallel(self, [S, R]);
	    self.nextSlice = 1;
	},
	function() {
	    console.log("Main: PAR returned.");
	    self.finish();
	}
    ];
});
//////

M = new Main();
M.start()

__RT.main();
