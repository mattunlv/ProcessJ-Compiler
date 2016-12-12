package CodeGeneratorJava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import AST.AST;
import AST.AltCase;
import AST.AltStat;
import AST.Annotations;
import AST.ArrayAccessExpr;
import AST.ArrayLiteral;
import AST.ArrayType;
import AST.Assignment;
import AST.BinaryExpr;
import AST.Block;
import AST.BreakStat;
import AST.CastExpr;
import AST.ChannelEndExpr;
import AST.ChannelEndType;
import AST.ChannelReadExpr;
import AST.ChannelType;
import AST.ChannelWriteStat;
import AST.ClaimStat;
import AST.Compilation;
import AST.ConstantDecl;
import AST.ContinueStat;
import AST.DoStat;
import AST.ExprStat;
import AST.Expression;
import AST.ExternType;
import AST.ForStat;
import AST.IfStat;
import AST.Invocation;
import AST.LocalDecl;
import AST.Name;
import AST.NameExpr;
import AST.NamedType;
import AST.NewArray;
import AST.ParBlock;
import AST.ParamDecl;
import AST.PrimitiveLiteral;
import AST.PrimitiveType;
import AST.ProcTypeDecl;
import AST.ProtocolCase;
import AST.ProtocolLiteral;
import AST.ProtocolTypeDecl;
import AST.RecordAccess;
import AST.RecordLiteral;
import AST.RecordMember;
import AST.RecordTypeDecl;
import AST.ReturnStat;
import AST.Sequence;
import AST.SkipStat;
import AST.Statement;
import AST.SwitchGroup;
import AST.SwitchLabel;
import AST.SwitchStat;
import AST.SyncStat;
import AST.Ternary;
import AST.TimeoutStat;
import AST.Type;
import AST.UnaryPostExpr;
import AST.UnaryPreExpr;
import AST.Var;
import AST.WhileStat;
import ProcessJ.runtime.PJAlt;
import ProcessJ.runtime.PJBarrier;
import ProcessJ.runtime.PJChannel;
import ProcessJ.runtime.PJMany2ManyChannel;
import ProcessJ.runtime.PJMany2OneChannel;
import ProcessJ.runtime.PJOne2ManyChannel;
import ProcessJ.runtime.PJOne2OneChannel;
import ProcessJ.runtime.PJProtocolCase;
import ProcessJ.runtime.PJTimer;
import Utilities.Error;
import Utilities.Log;
import Utilities.SymbolTable;
import Utilities.Visitor;

public class CodeGeneratorJava<T extends Object> extends Visitor<T> {

    /**
     * String Template grammar file location.
     */
    private final String _stGrammarFile = "StringTemplates/grammarTemplatesJava.stg";

    /**
     * String Template object to hold all templates.
     */
    private STGroup _stGroup;

    /**
     * Constant for no block wrap around invocation.
     */
    private static final int INV_WRAP_NONE = -1;

    /**
     * Constant denoting the invocation is inside par block.
     */
    private static final int INV_WRAP_PAR = 0;

    /**
     * Constant denoting the invocation is inside parfor block.
     */
    private static final int INV_WRAP_PARFOR = 1;

    /**
     * Reading channel-end constant.
     */
    private static final int CHAN_READ_END = 1;

    /**
     * Writing channel-end constant.
     */
    private static final int CHAN_WRITE_END = 2;

    /**
     * Signature of the main method in ProcessJ. - proc void main(string[] args)
     */
    private static final String MAIN_SIGNATURE = "([T;)V";

    /**
     * Code line delimiter.
     */
    private static final String DELIM = ";";

    /**
     * Channel end type. - Can be CHAN_READ_END or CHAN_WRITE_END
     */
    private int _chanEndType = 0;

    /**
     * Channel base type.
     */
    private String _chanBaseType = null;

    /**
     * Variable unique identifier.
     */
    private int _varId = 0;

    /**
     * Par block unique identifier.
     */
    private int _parId = 0;

    /**
     * Alt block unique identifier.
     */
    private int _altId = 1;

    /**
     * Resume point counter.
     */
    private int _jumpCnt = 1;

    /**
     * Flag denoting channel read end.
     */
    private boolean _isReadingChannelEnd = false;

    /**
     * User working directory set in Settings file.
     */
    private String _workdir = null;

    /**
     * The ProcessJ source filename.
     */
    private String _sourceFilename = null;

    /**
     * Current par block name.
     */
    private String _currParName = null;

    /**
     * Current proc name.
     */
    private String _currProcName = null;

    /**
     * Current protocol name.
     */
    private String _currProtocolName = null;

    /**
     * Top level declarations.
     */
    private SymbolTable _topLevelDecls = null;

    /**
     * List of all locals and formals transformed to fields.
     */
    private List<String> _fields = null;

    /**
     * List of switch cases.
     */
    private List<String> _switchCases = null;

    /**
     * List of barriers.
     */
    private List<String> _barriers = new ArrayList<String>();

    /**
     * List of record member names.
     */
    private List<String> _recMemberNames = null;

    /**
     * Map of channel name and its end-type.
     */
    private Map<String, Integer> _chanNameToEndType = new HashMap<String, Integer>();

    /**
     * Map of channel name and the type of data it carries (base type) .
     */
    private Map<String, String> _chanNameToBaseType = new HashMap<String, String>();

    /**
     * Map of parameter name and its modified field name.
     */
    private Map<String, String> _paramNameToFieldName = null;

    /**
     * Map of local var name and its modified field name.
     */
    private Map<String, String> _localNameToFieldName = null;

    /**
     * Map of protocol name and its modified name.
     */
    private Map<String, String> _protoNameToPrefixedName = new HashMap<String, String>();

    /**
     * Map of protocol tag name and its modified name.
     */
    private Map<String, String> _protoTagNameToPrefixedName = new HashMap<String, String>();

    /**
     * Map of record name and its modified name.
     */
    private Map<String, String> _recNameToPrefixedName = new HashMap<String, String>();

    /**
     * Map of protocol tag name and the protocol name that it belongs to.
     */
    private Map<String, String> _protoTagNameToProtoName = new HashMap<String, String>();

    /**
     * Map of ProcessJ type to its external type.
     */
    private Map<String, String> _pjTypeToExternType = new HashMap<String, String>();

    /**
     * List of generated block codes for invocation parameters.
     */
    private List<String> invParamBlocks = null;
    
    /**
     * List of generated binary expression blocks.
     */
    private List<String> binExprBlocks = new ArrayList<String>();

    /**
     * Map of protocol name and the corresponding tag that the switch label is currently using as const. expression.
     * Used in visitRecordAccess to do correct type castings.
     */
    private Map<String, String> _protoNameToProtoTagSwitchedOn = new HashMap<String, String>();

    public CodeGeneratorJava(SymbolTable topLevelDecls) {
        Log.log("==========================================");
        Log.log("* C O D E   G E N E R A T O R  ( Java )  *");
        Log.log("*        F I R S T  P A S S              *");
        Log.log("=========================================");

        this._stGroup = new STGroupFile(_stGrammarFile);
        this._topLevelDecls = topLevelDecls;
        State.init();
    }

    public void setSourceFilename(String n) {
        this._sourceFilename = n;
    }

    public void setWorkingDirectory(String w) {
        this._workdir = w;
    }

    public T visitAltCase(AltCase ac) {
        Log.log(ac.line + ": Visiting an AltCase");
        ST template = _stGroup.getInstanceOf("AltCase");

        //FIXME: figure out if this visitor is needed as well as the ST.template.
        Statement caseExprStmt = ac.guard().guard();
        Statement stat = ac.stat();
        String caseExprStr;

        return (T) template.render();
    }

    public T visitAltStat(AltStat as) {
        Log.log(as.line + ": Visiting an AltStat");

        boolean oldAlt = State.set(State.ALT, true);

        ST template = _stGroup.getInstanceOf("AltStat");

        Sequence<AltCase> altCaseList = as.body();
        int caseCount = altCaseList.size();

        /*
         * Creating boolean guards array. Refactor this.
         */
        ST altBGArrTemplate = _stGroup.getInstanceOf("AltBooleanGuardsArr");
        String[] constants = new String[caseCount];
        List<String> tempExprs = new ArrayList<String>();

        for (int i = 0; i < caseCount; i++) {

            AltCase ac = altCaseList.child(i);

            if (ac.precondition() == null) {

                constants[i] = String.valueOf(true);

            } else if (ac.precondition().isConstant()) {

                constants[i] = (String) ac.precondition().visit(this);

            } else {

                String tempVar = "bTemp" + i;
                Name n = new Name(tempVar);

                tempExprs.add((String) new LocalDecl(
                                                new PrimitiveType(PrimitiveType.BooleanKind), 
                                                new Var(n, ac.precondition()), 
                                                false
                                            ).visit(this)
                                          );

                constants[i] = (String) n.visit(this);
            }
        }

        altBGArrTemplate.add("tempExprs", tempExprs);
        altBGArrTemplate.add("constants", constants);
        altBGArrTemplate.add("altCnt", _altId);

        /*
         * Creating actual guards array
         */
        ST altGuardArrTemplate = _stGroup.getInstanceOf("AltGuardsArr");
        String[] guards = new String[caseCount];
        List<String> timers = new ArrayList<String>();
        List<String> timerNames = new ArrayList<String>();
        List<String> altCases = new ArrayList<String>();

        List<String> statementList = null;

        for (int i = 0; i < caseCount; i++) {

            ST altCase = _stGroup.getInstanceOf("AltCase");

            statementList = new ArrayList<String>();
            String[] stats = null;

            AltCase ac = altCaseList.child(i);
            Statement caseExprStmt = ac.guard().guard();

            if (caseExprStmt instanceof TimeoutStat) {
                TimeoutStat ts = (TimeoutStat) caseExprStmt;
                String tn = (String) ts.timer().visit(this);//name of timer
                guards[i] = tn;
                timerNames.add(tn);

                boolean oldAltGuard = State.set(State.ALT_GUARD, true);
                timers.add((String) ts.visit(this));
                State.set(State.ALT_GUARD, oldAltGuard);

                stats = getStatements(ac.stat());
                statementList.addAll(Arrays.asList(stats)); //the statements in the timer

            } else if (caseExprStmt instanceof ExprStat) { //Channel
                Expression e = ((ExprStat) caseExprStmt).expr();
                ChannelReadExpr cr = null;
                if (e instanceof ChannelReadExpr) {
                    cr = (ChannelReadExpr) e;
                } else if (e instanceof Assignment) {
                    cr = (ChannelReadExpr) ((Assignment) e).right();
                }
                guards[i] = (String) cr.channel().visit(this); //name of channel

                boolean oldAltGuard = State.set(State.ALT_GUARD, true);
                String chanRead = (String) caseExprStmt.visit(this);
                State.set(State.ALT_GUARD, oldAltGuard);

                statementList.add(chanRead);

                stats = getStatements(ac.stat());
                statementList.addAll(Arrays.asList(stats)); //the statements in the channelexpr guard 

            } else if (caseExprStmt instanceof SkipStat) {
                guards[i] = PJAlt.class.getSimpleName() + ".SKIP_GUARD";

                stats = getStatements(ac.stat());
                statementList.addAll(Arrays.asList(stats)); //the statements in the skip guard
            } else {
                Error.error("Unknown statement in guards!!!");
            }

            altCase.add("number", i);
            altCase.add("statementList", statementList);

            altCases.add(altCase.render());
        }

        /*
         * Collecting timers and generating start and kill code
         */
        List<String> timerStarts = new ArrayList<String>();
        List<String> timerKills = new ArrayList<String>();

        for (String t : timerNames) {

            ST timerStart = _stGroup.getInstanceOf("AltTimerStart");
            timerStart.add("timer", t);

            timerStarts.add(timerStart.render());

            ST timerKill = _stGroup.getInstanceOf("AltTimerKill");
            timerKill.add("timer", t);

            timerKills.add(timerKill.render());
        }

        /*
         * Creating timer start cases
         */
        if (timerStarts.size() > 0) {
            ST altCase = _stGroup.getInstanceOf("AltCase");
            altCase.add("number", -1);
            altCase.add("statementList", timerStarts);
            altCases.add(altCase.render());
        }

        String fieldNameChosen = Helper.convertToFieldName("chosen", false, this._varId++);
        _fields.add("int " + fieldNameChosen);
        template.add("chosen", fieldNameChosen);

        ST altSwitchGroup = _stGroup.getInstanceOf("AltSwitchGroup");
        altSwitchGroup.add("cases", altCases);
        altSwitchGroup.add("chosen", fieldNameChosen);

        if (guards.length > 0) {
            altGuardArrTemplate.add("guards", guards);
            altGuardArrTemplate.add("altCnt", _altId);
        }

        template.add("timers", timers);
        template.add("caseCount", caseCount);
        template.add("altSwitchGroup", altSwitchGroup.render());
        template.add("initBoolGuards", altBGArrTemplate.render());
        template.add("initGuards", altGuardArrTemplate.render());
        template.add("timerKills", timerKills);

        template.add("jmp1", _jumpCnt);
        _switchCases.add(renderLookupSwitchCase(_jumpCnt));
        _jumpCnt++;

        template.add("jmp2", _jumpCnt);
        _switchCases.add(renderLookupSwitchCase(_jumpCnt));
        _jumpCnt++;

        String fieldNameAlt = Helper.convertToFieldName("alt", false, this._varId++);
        _fields.add(PJAlt.class.getSimpleName() + " " + fieldNameAlt);
        template.add("name", fieldNameAlt);

        template.add("altCnt", _altId);
        _altId++;

        State.set(State.ALT, oldAlt);

        return (T) template.render();
    }

    /**
     * ArrayAccessExpr
     */
    public T visitArrayAccessExpr(ArrayAccessExpr ae) {
        Log.log(ae.line + ": Visiting an ArrayAccessExpr!");
        String myArrayTarget = (String) ae.target().visit(this);
        String myIndex = (String) ae.index().visit(this);

        return (T) ("(" + myArrayTarget + "[" + myIndex + "])");
    }

    /**
     * ArrayLiteral
     */
    public T visitArrayLiteral(ArrayLiteral al) {
        Log.log(al.line + ": Visting ArrayLiteral.");
        return (T) al.elements().visit(this);
    }

    /**
     * ArrayType
     */
    public T visitArrayType(ArrayType at) {
        Log.log(at.line + ": Visiting an ArrayType!");
        String baseType = (String) at.baseType().visit(this);
        return (T) (baseType + "[]");
    }

    /**
     * ChannelType
     */
    public T visitChannelType(ChannelType ct) {
        Log.log(ct.line + ": Visiting a Channel Type!");

        String typeString = null;
        
        switch (ct.shared()) {
            case ChannelType.NOT_SHARED:
                typeString = PJOne2OneChannel.class.getSimpleName();
                break;
            case ChannelType.SHARED_WRITE:
                typeString = PJMany2OneChannel.class.getSimpleName();
                break;
            case ChannelType.SHARED_READ:
                typeString = PJOne2ManyChannel.class.getSimpleName();
                break;
            case ChannelType.SHARED_READ_WRITE:
                typeString = PJMany2ManyChannel.class.getSimpleName();
                break;
        }

        Type t = ct.baseType();
        _chanBaseType = getChannelBaseType(t);

        return (T) (typeString + "<" + _chanBaseType + ">");
    }

    /**
     * ChannelEndExpr
     */
    public T visitChannelEndExpr(ChannelEndExpr ce) {
        Log.log(ce.line + ": Visiting a Channel End Expression!");

        String channel = (String) ce.channel().visit(this);

        if (ce.isRead()) {
            _isReadingChannelEnd = true;
        } else if (ce.isWrite()) {
            _isReadingChannelEnd = false;
        }

        return (T) channel;
    }

    /**
     * ChannelEndType
     */
    public T visitChannelEndType(ChannelEndType ct) {
        Log.log(ct.line + ": Visiting a Channel End Type!");

        //Getting the channel type
        String maintype = PJChannel.class.getSimpleName();
        if (ct.isShared()) {
            if (ct.isRead()) {
                maintype = PJOne2ManyChannel.class.getSimpleName();
            } else {
                maintype = PJMany2OneChannel.class.getSimpleName();
            }
        } else {
            maintype = PJOne2OneChannel.class.getSimpleName();
        }

        _chanBaseType = getChannelBaseType(ct.baseType());

        String chanEndType = "";
        if (State.is(State.PARAMS)) {
            chanEndType = PJChannel.class.getSimpleName() + "<" + _chanBaseType + ">";
        } else {
            chanEndType = maintype + "<" + _chanBaseType + ">";
        }

        if (ct.isRead()) {
            _chanEndType = CHAN_READ_END;
        } else if (ct.isWrite()) {
            _chanEndType = CHAN_WRITE_END;
        }

        return (T) chanEndType;
    }

    /**
     * ChannelReadExpr
     */
    public T visitChannelReadExpr(ChannelReadExpr cr) {
        Log.log(cr.line + ": Visiting ChannelReadExpr");
        return (T) createChannelReadExpr(null, null, cr);
    }

    /**
     * ChannelWriteStat
     */
    public T visitChannelWriteStat(ChannelWriteStat cw) {
        Log.log(cw.line + ": Visiting a Channel Write Statement!");

        ST template = _stGroup.getInstanceOf("ChannelWriteStat");

        NameExpr channelNameExpr = null;
        /*
         * Can either be NameExpression (chan.write(x)) or
         * ChannelEndExpr (chan.write.write(x))
         */
        Expression channelExpr = cw.channel();

        if (channelExpr instanceof NameExpr) {
            channelNameExpr = (NameExpr) channelExpr;
        } else if (channelExpr instanceof ChannelEndExpr) {
            channelNameExpr = (NameExpr) ((ChannelEndExpr) channelExpr).channel();
        }

        Type myType = null;
        if (channelNameExpr.myDecl instanceof LocalDecl) {
            myType = ((LocalDecl) channelNameExpr.myDecl).type();
        } else if (channelNameExpr.myDecl instanceof ParamDecl) {
            myType = ((ParamDecl) channelNameExpr.myDecl).type();
        }

        if (myType.isChannelEndType()) {
            ChannelEndType chanType = (ChannelEndType) myType;
            template.add("shared", chanType.isShared());
        } else if (myType.isChannelType()) {
            ChannelType chanType = (ChannelType) myType;
            if (chanType.shared() == ChannelType.SHARED_WRITE || chanType.shared() == ChannelType.SHARED_READ_WRITE) {
                template.add("shared", true);
            } else {
                template.add("shared", false);
            }
        }

        String channel = (String) channelExpr.visit(this);
        String expr = null;
        List<String> argBlocks = new ArrayList<String>();
        String argBlock = null;

        if (cw.expr() instanceof Assignment) {

            Assignment a = (Assignment) cw.expr();
            argBlock = (String) a.visit(this);
            argBlocks.add(argBlock);
            expr = (String) a.left().visit(this);

        } else if (cw.expr() instanceof BinaryExpr) {

            BinaryExpr be = (BinaryExpr) cw.expr();
            expr = Helper.convertToFieldName("temp", false, this._varId++);
            _fields.add(be.type.visit(this) + " " + expr);

            argBlock = (String) new Assignment(new NameExpr(new Name(expr)), be, Assignment.EQ).visit(this);
            argBlocks.add(argBlock);

        } else if (cw.expr() instanceof ChannelReadExpr) {

            ChannelReadExpr cr = (ChannelReadExpr) cw.expr();
            expr = Helper.convertToFieldName("temp", false, this._varId++);

            _fields.add(_chanNameToBaseType.get(cr.channel().visit(this)) + " " + expr);

            argBlock = (String) createChannelReadExpr(expr, "=", cr);
            argBlocks.add(argBlock);

        } else {
            State.set(State.CHAN_WRITE_VALUE, true);

            expr = (String) cw.expr().visit(this);

            State.set(State.CHAN_WRITE_VALUE, false);
        }

        template.add("argBlocks", argBlocks);
        template.add("channel", channel);
        template.add("expr", expr);

        /*
         * Adding switch cases for resumption.
         */
        for (int i = 0; i < 2; i++) {
            _switchCases.add(renderLookupSwitchCase(_jumpCnt));
            template.add("jmp" + i, _jumpCnt);
            _jumpCnt++;
        }

        return (T) template.render();
    }

    /**
     * Compilation TODO add the pragmas, packageName and imports later!
     */
    public T visitCompilation(Compilation c) {
        Log.log(c.line + ": Visiting the Compilation");

        ST template = _stGroup.getInstanceOf("Compilation");

        /*
         * Visiting type decls will be done in ordered sequence
         * of RecordTypeDecls, ProtocolTypeDecls and ProcTypeDecls
         * so that forward-referencing for NamedType can be done.
         */
        List<String> typeDeclsStr = new ArrayList<String>();
        Sequence<Type> individualDecls = new Sequence<Type>();
        Sequence<Type> extendedDecls = new Sequence<Type>();

        Sequence<Type> typeDecls = c.typeDecls();

        /*
         * Collect RecordTypeDecls.
         */
        Iterator<Type> it1 = typeDecls.iterator();
        while (it1.hasNext()) {
            AST temp = it1.next();

            if (temp instanceof ConstantDecl) {
                continue;
            }

            Type type = (Type) temp;
            if (type instanceof RecordTypeDecl) {
                RecordTypeDecl rd = (RecordTypeDecl) type;
                if (rd.extend() == null || rd.extend().size() == 0) {
                    individualDecls.append(rd);
                } else {
                    extendedDecls.append(rd);
                }
                it1.remove();
            }
        }

        /*
         * Visit non-extended and then extended records.
         */
        Collections.sort((List<Type>) extendedDecls.children, new Comparator<Type>() {
            @Override
            public int compare(Type o1, Type o2) {
                return ((RecordTypeDecl) o1).extend().size() - ((RecordTypeDecl) o2).extend().size();
            }
        });

        typeDeclsStr.addAll(Arrays.asList((String[]) individualDecls.visit(this)));
        typeDeclsStr.addAll(Arrays.asList((String[]) extendedDecls.visit(this)));

        individualDecls.clear();
        extendedDecls.clear();

        /*
         * Collect ProtocolTypeDecls.
         */
        Iterator<Type> it2 = typeDecls.iterator();
        while (it2.hasNext()) {
            AST temp = it2.next();

            if (temp instanceof ConstantDecl) {
                continue;
            }

            Type type = (Type) temp;
            if (type instanceof ProtocolTypeDecl) {
                ProtocolTypeDecl pd = (ProtocolTypeDecl) type;
                if (pd.extend() == null || pd.extend().size() == 0) {
                    individualDecls.append(pd);
                } else {
                    extendedDecls.append(pd);
                }
                it2.remove();
            }
        }

        /*
         * Visit non-extended and then extended protocols.
         */
        Collections.sort((List<Type>) extendedDecls.children, new Comparator<Type>() {
            @Override
            public int compare(Type o1, Type o2) {
                return ((ProtocolTypeDecl) o1).extend().size() - ((ProtocolTypeDecl) o2).extend().size();
            }
        });

        typeDeclsStr.addAll(Arrays.asList((String[]) individualDecls.visit(this)));
        typeDeclsStr.addAll(Arrays.asList((String[]) extendedDecls.visit(this)));

        /*
         * Visit remaining items on the list which are all ProcTypeDecls.
         */
        typeDeclsStr.addAll(Arrays.asList((String[]) typeDecls.visit(this)));

        template.add("typeDecls", typeDeclsStr);
        template.add("packageName", this._sourceFilename);

        String home = System.getProperty("user.home");

        /*
         * Finally write the output to a file
         */
        String finalOutput = template.render();
        Helper.writeToFile(finalOutput, this._sourceFilename, this._workdir);

        Log.log("Output written to file " + this._sourceFilename);

        return (T) finalOutput;
    }

    public T visitClaimStat(ClaimStat cs) {
        Log.log(cs.line + ": Visiting an ClaimStat");

        State.set(State.CLAIMSTAT, true);

        ST template = null;

        Map<String, Boolean> chanNameToEndType = new HashMap<String, Boolean>();

        String ldStr = null;
        Sequence<AST> claimExprs = cs.channels();

        for (int k = 0; k < claimExprs.size(); k++) {
            AST ast = claimExprs.child(k);
            if (ast instanceof LocalDecl) {
                LocalDecl ld = (LocalDecl) ast;
                ldStr = (String) ld.visit(this);
                chanNameToEndType.put(ld.var().name().getname(), _isReadingChannelEnd);
            } else {
                chanNameToEndType.put((String) ast.visit(this), _isReadingChannelEnd);
            }

        }

        String[] stats = getStatements(cs.stat());

        template = _stGroup.getInstanceOf("ClaimStat");
        template.add("chanNameToReadEndType", chanNameToEndType);
        template.add("ldstr", ldStr);
        template.add("stats", stats);

        template.add("jmp", _jumpCnt);
        _switchCases.add(renderLookupSwitchCase(_jumpCnt));
        _jumpCnt++;

        State.set(State.CLAIMSTAT, false);

        return (T) template.render();
    }

    /**
     * Assignment
     */
    public T visitAssignment(Assignment as) {
        Log.log(as.line + ": Visiting an Assignment");

        ST template = _stGroup.getInstanceOf("Assignment");
        String left = (String) as.left().visit(this);
        String op = (String) as.opString();

        if (as.right() instanceof ChannelReadExpr) {
            return (T) createChannelReadExpr(left, op, (ChannelReadExpr) as.right());
        } else if (as.right() instanceof Invocation) {
            return (T) createInvocation(left, op, (Invocation) as.right(), false);
        } else if (as.right() instanceof NewArray) {
            return (T) createNewArray(left, (NewArray) as.right());
        } else if (as.right() instanceof BinaryExpr) {
            return (T) (createBinaryExpr(left, op, (BinaryExpr) as.right()) + DELIM);
            //TODO unaryExpr check might be needed.
        } else {
            String right = (String) as.right().visit(this);
            template.add("left", left);
            template.add("right", right);
            template.add("op", op);
            template.add("isForCtrl", State.is(State.FOR_LOOP_CONTROL));
        }

        return (T) template.render();
    }

    private T createNewArray(String left, NewArray ne) {
        Log.log(ne.line + ": Creating a NewArray");

        ST template = null;

        Type bt = ne.baseType();

        if (bt.isChannelType() || bt.isChannelEndType() || (bt.isNamedType() && (isProtocolType((NamedType) bt)))) {
            template = _stGroup.getInstanceOf("NewArrayIntializedElements");

            String parameterizedType = (String) ne.baseType().visit(this);
            String typeName = parameterizedType.substring(0, parameterizedType.indexOf('<')).trim();
            String[] dimsExpr = (String[]) ne.dimsExpr().visit(this);

            template.add("parameterizedType", parameterizedType);
            template.add("typeName", typeName);
            template.add("dimsExpr", dimsExpr);
        } else {
            template = _stGroup.getInstanceOf("NewArray");

            String type = (String) ne.baseType().visit(this);
            
            String[] init = null;
            if (ne.init() != null) {
                init = (String[]) ne.init().visit(this);
            }
            String[] dims = (String[]) ne.dimsExpr().visit(this);

            template.add("type", type);

            if (dims.length == 0) {
                template.add("dims", null);
            } else {
                template.add("dims", dims);
            }

            template.add("init", init);
        }

        template.add("left", left);

        return (T) template.render();
    }

    private T createInvocation(String left, String op, Invocation in, boolean isParamInvocation) {
        return createInvocation(left, op, in, null, isParamInvocation, INV_WRAP_NONE);
    }

    private T createInvocation(String left, String op, Invocation in, List<String> barriers, boolean isParamInvocation,
            int invocationWrapper) {
        Log.log(in.line + ": Creating Invocation (" + in.procedureName().getname() + ") with LHS as " + left);

        invParamBlocks = new ArrayList<String>();
        ST template = null;
        ProcTypeDecl pd = in.targetProc;
        String qualifiedPkg = Helper.getQualifiedPkg(pd, this._sourceFilename);

        String procName = null;
        if (pd.myPackage.contains(this._sourceFilename)) { //invoking proc in the same file
            procName = prefixProcName(in.procedureName().getname());
        } else {
            procName = in.procedureName().getname(); //invoking proc from import
        }
        String qualifiedProc = qualifiedPkg + "." + procName;

        List<String> paramLst = new ArrayList<String>();
        List<String> paramBlocks = new ArrayList<String>();

        boolean yields = Helper.isYieldingProc(pd);
        /*
         * For any invocation of yielding proc that's not inside
         * par block, we will, as inefficient as it, wrap it in
         * a par that has single exprstat.
         */
        if (yields && invocationWrapper != INV_WRAP_PAR && invocationWrapper != INV_WRAP_PARFOR) {
            return (new ParBlock(new Sequence(new ExprStat(in)), new Sequence())).visit(this);
        }

        boolean emptyBarriers = (barriers == null);

        /*
         * Generate param code.
         */
        Sequence<Expression> params = in.params();
        for (int i = 0; i < params.size(); i++) {

            if (emptyBarriers) {
                barriers = new ArrayList<String>();
            }

            Expression e = params.child(i);
            String fieldNameTemp = null;
            if (e != null) {
                if (e instanceof ChannelReadExpr) {

                    String typeString = in.targetProc.formalParams().child(i).type().typeName();
                    fieldNameTemp = Helper.convertToFieldName("temp", false, this._varId++);
                    _fields.add(typeString + " " + fieldNameTemp);
                    String chanReadBlock = (String) createChannelReadExpr(fieldNameTemp, "=", (ChannelReadExpr) e);

                    paramBlocks.add(chanReadBlock);
                    paramLst.add(fieldNameTemp);

                } else if (e instanceof Invocation) {

                    String typeString = ((Invocation) e).targetProc.returnType().typeName();
                    fieldNameTemp = Helper.convertToFieldName("temp", false, this._varId++);
                    _fields.add(typeString + " " + fieldNameTemp);
                    String invocationBlock = (String) createInvocation(fieldNameTemp, "=", (Invocation) e, true);

                    paramBlocks.add(invocationBlock);
                    paramLst.add(fieldNameTemp);

                } else if (e instanceof BinaryExpr) {
                    String typeString = (String) ((BinaryExpr) e).type.visit(this);
                    fieldNameTemp = Helper.convertToFieldName("temp", false, this._varId++);
                    _fields.add(typeString + " " + fieldNameTemp);
                    String binaryExprBlock = (String) createBinaryExpr(fieldNameTemp, "=", (BinaryExpr) e);

                    paramBlocks.add(binaryExprBlock);
                    paramLst.add(fieldNameTemp);
                } else {

                    State.set(State.INV_ARG, true);

                    String name = (String) e.visit(this);
                    
                    State.set(State.INV_ARG, true);

                    paramLst.add(name);

                    if (e instanceof NameExpr) {
                        NameExpr ne = (NameExpr) e;

                        Type t = null;

                        if (ne.myDecl instanceof LocalDecl) {
                            t = ((LocalDecl) ne.myDecl).type();
                        } else if (ne.myDecl instanceof ParamDecl) {
                            t = ((ParamDecl) ne.myDecl).type();
                        }

                        if (emptyBarriers) {
                            if (t.isBarrierType()) {
                                barriers.add(name);
                            }
                        }
                    }
                }
            }
        }

        template = _stGroup.getInstanceOf("InvocationNormal");
        template.add("qualifiedProc", qualifiedProc);
        template.add("procParams", paramLst);

        if (invocationWrapper == INV_WRAP_PAR) {
            template.add("par", true);
        } else if (invocationWrapper == INV_WRAP_PARFOR) {
            template.add("parfor", true);
        }

        template.add("parName", _currParName);
        template.add("barriers", barriers);

        /*
         * If target proc (pd) is a yielding proc, ie. it is a process,
         * instead of normal invocation, we need to instantiate the process
         * and schedule it.
         */
        template.add("isProcess", yields);

        if (left != null || !paramBlocks.isEmpty()) {

            String invocationBlock = template.render();
            template = _stGroup.getInstanceOf("InvocationWithInvocationParamType");

            if (invocationWrapper == INV_WRAP_PARFOR) {
                invParamBlocks.addAll(paramBlocks);
            } else {
                template.add("paramBlocks", paramBlocks);
            }
            template.add("left", left);
            template.add("op", op);
            template.add("right", invocationBlock);
        }

        return (T) template.render();

    }
    
    private T createChannelReadExpr(String left, String op, ChannelReadExpr cr) {
        Log.log(cr.line + ": Creating ChannelReadExpr with LHS as " + left);

        ST template = null;
        Expression channelExpr = cr.channel();
        NameExpr channelNameExpr = null;

        /*
         * Can either be NameExpression (chan.read()) or
         * ChannelEndExpr (chan.read.read())
         */
        if (channelExpr instanceof NameExpr) {
            channelNameExpr = (NameExpr) channelExpr;
        } else if (channelExpr instanceof ChannelEndExpr) {
            channelNameExpr = (NameExpr) ((ChannelEndExpr) channelExpr).channel();
        }

        String channel = (String) channelNameExpr.visit(this);
        Type myType = null;

        if (channelNameExpr.myDecl instanceof LocalDecl) {
            myType = ((LocalDecl) channelNameExpr.myDecl).type();
        } else if (channelNameExpr.myDecl instanceof ParamDecl) {
            myType = ((ParamDecl) channelNameExpr.myDecl).type();
        }

        if (myType.isTimerType()) {
            /*
             * Possibility One: Timer
             */
            template = _stGroup.getInstanceOf("TimerReadExpr");
            template.add("left", left);
            return (T) template.render();
        } else {
            Type baseType = null;

            /*
             * Possibility Two: This is an actual end: chan<type>.read chan,
             * chan.read()
             */
            if (myType.isChannelEndType()) {
                ChannelEndType chanType = (ChannelEndType) myType;
                baseType = chanType.baseType();
            }

            /*
             * Possibility Three: This is a channel to be treated as an end to avoid
             * chan.read.read(). 
             */
            else if (myType.isChannelType()) {
                ChannelType chanType = (ChannelType) myType;
                baseType = chanType.baseType();
            }

            //EXTENDED RENDEZVOUS
            Block b = cr.extRV();
            String[] extRv = null;
            if (b != null) {
                //FIXME looks like ill need to turn off altGuard flag for
                //this block as extRv could have read from the same channel
                //again.
                boolean oldAltGuard = State.set(State.ALT_GUARD, false);
                extRv = (String[]) b.visit(this);
                //        isAltGuard = oldAltGuard;
                State.set(State.ALT_GUARD, oldAltGuard);
            }
            //-------

            if (extRv == null) {
                template = _stGroup.getInstanceOf("ChannelReadExpr");
            } else {
                template = _stGroup.getInstanceOf("ChannelReadExprExtRv");
                template.add("extRv", extRv);
            }

            //Since channel read in Alts are handled differently, i.e. w/o
            //yields in the generated code but rather in Alt class,
            //we don't want to increment and add jumpCnts to runlabel switch.
            if (!(State.is(State.ALT) && State.is(State.ALT_GUARD))) {
                /*
                 * Adding switch cases for resumption.
                 */
                for (int i = 0; i < 2; i++) {
                    _switchCases.add(renderLookupSwitchCase(_jumpCnt));
                    template.add("jmp" + i, _jumpCnt);
                    _jumpCnt++;
                }
            }

            template.add("channel", channel);
            template.add("op", op);
            template.add("left", left);
            //FIXME I might not even need _inAlt here.
            template.add("alt", (State.is(State.ALT) && State.is(State.ALT_GUARD)));

        }

        return (T) template.render();
    }

    /**
     * BreakStat TODO: Add identifier option.
     */
    public T visitBreakStat(BreakStat bs) {
        Log.log(bs.line + ": Visiting a BreakStat");
        ST template = _stGroup.getInstanceOf("BreakStat");

        Name name = bs.target();
        if (name != null) {
            String nameStr = (String) name.visit(this);
            // TODO: Add name option here.
        }

        return (T) template.render();
    }

    /**
     * BinaryExpr
     */
    public T visitBinaryExpr(BinaryExpr be) {
        Log.log(be.line + ": Visiting a Binary Expression");
        return createBinaryExpr(null, null, be);
    }

    private T createBinaryExpr(String lhs, String lhsOp, BinaryExpr be) {
        ST template = _stGroup.getInstanceOf("BinaryExpr");

        List<String> exprBlocks = new ArrayList<String>();
        String exprBlock = "";
        String left = null;
        String right = null;

        if (be.left() instanceof ChannelReadExpr) {
            ChannelReadExpr cr = (ChannelReadExpr) be.left();
            left = Helper.convertToFieldName("temp", false, this._varId++);
            
            _fields.add(_chanNameToBaseType.get(cr.channel().visit(this)) + " " + left);

            exprBlock = (String) createChannelReadExpr(left, "=", cr);

            if (State.is(State.FOR_LOOP_CONTROL)) {
                binExprBlocks.add(exprBlock);
            } else {
                exprBlocks.add(exprBlock);
            }
        } else if (be.left() instanceof BinaryExpr && !State.is(State.FOR_LOOP_CONTROL)) {
            BinaryExpr lbe = (BinaryExpr) be.left();
            left = Helper.convertToFieldName("temp", false, this._varId++);
            _fields.add(lbe.type.visit(this) + " " + left);

            exprBlock = (String) createBinaryExpr(left, "=", lbe) + DELIM;
            
            if (State.is(State.IF_ELSE_PREDICATE)) {
                binExprBlocks.add(exprBlock);
            } else {
                exprBlocks.add(exprBlock);
            }
            
        } else {
            left = (String) be.left().visit(this);
        }

        if (be.right() instanceof ChannelReadExpr) {
            ChannelReadExpr cr = (ChannelReadExpr) be.right();
            right = Helper.convertToFieldName("temp", false, this._varId++);
            _fields.add(_chanNameToBaseType.get(cr.channel().visit(this)) + " " + right);

            exprBlock = (String) createChannelReadExpr(right, "=", cr);

            if (State.is(State.FOR_LOOP_CONTROL)) {
                binExprBlocks.add(exprBlock);
            } else {
                exprBlocks.add(exprBlock);
            }
        } else if (be.right() instanceof BinaryExpr && !State.is(State.FOR_LOOP_CONTROL)) {
            BinaryExpr rbe = (BinaryExpr) be.right();
            right = Helper.convertToFieldName("temp", false, this._varId++);
            _fields.add(rbe.type.visit(this) + " " + right);

            exprBlock = (String) createBinaryExpr(right, "=", rbe) + DELIM;
            
            if (State.is(State.IF_ELSE_PREDICATE)) {
                binExprBlocks.add(exprBlock);
            } else {
                exprBlocks.add(exprBlock);
            }
        } else {
            right = (String) be.right().visit(this);
        }

        String op = (String) be.opString();
        
        template.add("lhs", lhs);
        template.add("lhsOp", lhsOp);
        template.add("exprBlocks", exprBlocks);

        template.add("left", left);
        template.add("right", right);
        template.add("op", op);

        return (T) template.render();
    }

    /**
     * Block
     */
    public T visitBlock(Block bl) {
        Log.log(bl.line + ": Visiting a Block");
        String[] statements = (String[]) bl.stats().visit(this);

        return (T) statements;
    }

    /**
     * CastExpr
     */
    public T visitCastExpr(CastExpr ce) {
        Log.log(ce.line + ": Visiting a Cast Expression");
        ST template = _stGroup.getInstanceOf("CastExpr");
        String ct = ce.type().typeName();
        String expr = (String) ce.expr().visit(this);

        template.add("ct", ct);
        template.add("expr", expr);

        return (T) template.render();
    }

    /**
     * ContinueStat TODO: add identifier option.
     */
    public T visitContinueStat(ContinueStat cs) {
        Log.log(cs.line + ": Visiting a ContinueStat");

        ST template = _stGroup.getInstanceOf("ContinueStat");
        // Can be null.
        Name name = cs.target();

        if (name != null) {
            String nameStr = (String) name.visit(this);
            // TODO: Add name option here.
        }

        return (T) template.render();
    }

    /**
     * DoStat
     */
    public T visitDoStat(DoStat ds) {
        Log.log(ds.line + ": Visiting a DoStat");

        ST template = _stGroup.getInstanceOf("DoStat");
        
        updateForeverLoop(ds.foreverLoop);
        
        List<String> exprLst = new ArrayList<String>();
        List<String> exprBlocks = new ArrayList<String>();
        String fieldNameTemp = null;

        Expression e = ds.expr();

        if (e != null) {
            if (e instanceof ChannelReadExpr) {

                ChannelReadExpr cr = (ChannelReadExpr) e;
                String typeString = _chanNameToBaseType.get(cr.channel().visit(this));
                fieldNameTemp = Helper.convertToFieldName("temp", false, this._varId++);
                _fields.add(typeString + " " + fieldNameTemp);
                String chanReadBlock = (String) createChannelReadExpr(fieldNameTemp, "=", (ChannelReadExpr) e);

                exprBlocks.add(chanReadBlock);
                exprLst.add(fieldNameTemp);

            } else if (e instanceof Invocation) {

                String typeString = ((Invocation) e).targetProc.returnType().typeName();
                fieldNameTemp = Helper.convertToFieldName("temp", false, this._varId++);
                _fields.add(typeString + " " + fieldNameTemp);
                String invocationBlock = (String) createInvocation(fieldNameTemp, "=", (Invocation) e, true);

                exprBlocks.add(invocationBlock);
                exprLst.add(fieldNameTemp);

            } else if (e instanceof BinaryExpr) {
                String typeString = (String) ((BinaryExpr) e).type.visit(this);
                fieldNameTemp = Helper.convertToFieldName("temp", false, this._varId++);
                _fields.add(typeString + " " + fieldNameTemp);
                String binaryExprBlock = (String) createBinaryExpr(fieldNameTemp, "=", (BinaryExpr) e);

                exprBlocks.add(binaryExprBlock);
                exprLst.add(fieldNameTemp);
            } else {

                State.set(State.WHILE_EXPR, true);

                String name = (String) e.visit(this);
                
                State.set(State.WHILE_EXPR, true);

                exprLst.add(name);

            } 
        }
        
        String[] stats = (String[]) ds.stat().visit(this);
        template.add("stat", stats);
        
        template.add("exprBlocks", exprBlocks);
        template.add("exprLst", exprLst);

        return (T) template.render();
    }

    /**
     * ExprStat
     */
    public T visitExprStat(ExprStat es) {
        Log.log(es.line + ": Visiting a ExprStat");

        return (T) es.expr().visit(this);
    }

    /**
     * ExternType
     */
    public T visitExternType(ExternType et) {
        Log.log(et.line + ": Visiting an ExternType (" + et.name() + ")");
        return (T) et.name().getname();
    }

    /**
     * ForStat
     */
    public T visitForStat(ForStat fs) {
        Log.log(fs.line + ": Visiting a ForStat");

        ST template = _stGroup.getInstanceOf("ForStat");

        String[] initStr = null;
        String[] incrStr = null;
        String expr = null;

        updateForeverLoop(fs.foreverLoop);

        Sequence<Statement> init = fs.init();

        List<String> binExprBlocks = new ArrayList<String>();

        State.set(State.FOR_LOOP_CONTROL, true);
        if (init != null) {
            initStr = (String[]) init.visit(this);
        }

        Sequence<ExprStat> incr = fs.incr();
        if (incr != null) {
            incrStr = (String[]) incr.visit(this);
        }

        Expression e = fs.expr();
        if (e != null) {
            expr = (String) fs.expr().visit(this);
        }

        if (this.binExprBlocks.size() > 0) {
            binExprBlocks.addAll(this.binExprBlocks);

            this.binExprBlocks.clear();
        }

        State.set(State.FOR_LOOP_CONTROL, false);

        String object = null;
        boolean emptyBlock = false;

        if (fs.isPar()) {

            if(fs.stats() instanceof Block && ((Block) fs.stats()).stats().size() == 0) {
               emptyBlock = true; 
            }
            
            boolean oldVal = false;
            String nameHolder = null;
            List<String> barriersTemp = null; 
            Sequence<Expression> barriers = null;

            if (!emptyBlock) {

                oldVal = State.set(State.PARFOR, true);
                template = _stGroup.getInstanceOf("ParForStat");

                nameHolder = _currParName; 
                _currParName = "parfor" + ++_parId; 

                barriers = fs.barriers();
                if (barriers != null) {
                    barriersTemp = new ArrayList<String>();
                    barriersTemp.addAll(_barriers);
                    _barriers.clear();
                    _barriers.addAll(Arrays.asList((String[]) barriers.visit(this)));
                }
            }
            
            String rendered = null;
            List<String> invocationParamBlocks = new ArrayList<String>();

            if (fs.stats() instanceof Block) {

                Block b = (Block) fs.stats();

                if (!emptyBlock) {

                    if (b.stats().size() == 1 
                            && b.stats().child(0) instanceof ExprStat
                            && ((ExprStat) b.stats().child(0)).expr() instanceof Invocation) {

                        Invocation in = (Invocation) ((ExprStat) b.stats().child(0)).expr();
                        ProcTypeDecl pd = in.targetProc;

                        if (Helper.isYieldingProc(pd)) {
                            rendered = (String) createInvocation(
                                    null, 
                                    null, 
                                    in, 
                                    _barriers, 
                                    false,
                                    INV_WRAP_PARFOR
                                );

                            invocationParamBlocks.addAll(invParamBlocks);

                        } else {
                            rendered = (String) (createAnonymousProcTypeDecl(b).visit(this)); 
                        }

                    } else {

                        rendered = (String) (createAnonymousProcTypeDecl(b).visit(this));
                       
                    } 
                }
                
            } else if (fs.stats() instanceof ExprStat) {

                ExprStat es = (ExprStat) fs.stats();

                if (es != null) {
                    if (es.expr() instanceof Invocation) {

                        Invocation in = (Invocation) es.expr();
                        ProcTypeDecl pd = in.targetProc;

                        if (Helper.isYieldingProc(pd)) {
                            rendered = (String) createInvocation(
                                    null, 
                                    null, 
                                    in, 
                                    _barriers, 
                                    false,
                                    INV_WRAP_PARFOR
                                );
                        } else {
                            rendered = (String) (createAnonymousProcTypeDecl(fs.stats()).visit(this)); 
                        }
                        
                        invocationParamBlocks.addAll(invParamBlocks);

                    } else {

                        rendered = (String) (createAnonymousProcTypeDecl(fs.stats()).visit(this));

                    }
                }
                

            } else if (fs.stats() != null){

                rendered = (String) (createAnonymousProcTypeDecl(fs.stats()).visit(this));

            }

            template.add("stats", rendered);

            if (!emptyBlock) {
                template.add("parName", _currParName);
                template.add("barriers", _barriers);
                
                /*
                 * Adding resumption points
                 */
                template.add("jmp", _jumpCnt);
                _switchCases.add(renderLookupSwitchCase(_jumpCnt));
                _jumpCnt++;

                if (invocationParamBlocks.size() > 0) { 
                    template.add("invParamBlocks", invocationParamBlocks);
                } 
            }

            template.add("init", initStr);
            template.add("incr", incrStr);
            template.add("expr", expr);

            template.add("binExprBlocks", binExprBlocks);

            object = template.render();

            if (!emptyBlock) {
                _currParName = nameHolder;
                if (barriersTemp != null) {
                    _barriers.clear();
                    _barriers.addAll(barriersTemp);
                }
                State.set(State.PARFOR, oldVal);
            }
            
        } else {
            template = _stGroup.getInstanceOf("ForStat");
            /*
             * Depending whether there is curly brackets (block) it
             * may return an array, or maybe just a single object. 
             */
            Statement st = fs.stats();
            Object stats = null;
            if (st != null) {
                stats = fs.stats().visit(this);
            }

            if (stats instanceof String[]) {
                template.add("stats", (String[]) stats);
            } else {
                template.add("stats", (String) stats);
            }

            template.add("init", initStr);
            template.add("incr", incrStr);
            template.add("expr", expr);

            template.add("binExprBlocks", binExprBlocks);

            object = template.render();
        }

        return (T) object;
    }

    /**
     * Invocation.
     */
    public T visitInvocation(Invocation in) {
        Log.log(in.line + ": Visiting Invocation (" + in.procedureName().getname() + ")");
        return (T) createInvocation(null, null, in, false);
    }

    public T visitConstantDecl(ConstantDecl cd) {
        Log.log(cd.line + ": Visting ConstantDecl (" + cd.type().typeName() + " " + cd.var().name().getname() + ")");

        ST template = _stGroup.getInstanceOf("ConstantDecl");

        template.add("type", cd.type().visit(this));
        template.add("var", cd.var().visit(this));

        return (T) template.render();
    }

    /**
     * LocalDecl
     */
    public T visitLocalDecl(LocalDecl ld) {
        Log.log(ld.line + ": Visting LocalDecl (" + ld.type().typeName() + " " + ld.var().name().getname() + ")");

        globalizeAndStoreLocalDeclName(ld.var().name());

        String typeString = (String) ld.type().visit(this);
        String name = (String) ld.var().name().visit(this);

        if (ld.type() instanceof ChannelEndType
                || ((ld.type() instanceof ArrayType) && (((ArrayType) ld.type()).baseType() instanceof ChannelEndType))) {
            _chanNameToEndType.put(name, _chanEndType);
            _chanNameToBaseType.put(name, _chanBaseType);
        } else if (ld.type().isTimerType()) {
            _chanNameToBaseType.put(name, "long");
        }

        boolean isTimerType = false;
        if (ld.type() instanceof PrimitiveType) {
            isTimerType = ((PrimitiveType) ld.type()).isTimerType();
        }

        boolean isProtoType = false;
        boolean isRecType = false;
        if (ld.type() instanceof NamedType) { //Protocol or Record type
            isProtoType = isProtocolType((NamedType) ld.type());
            isRecType = isRecordType((NamedType) ld.type());
        }

        if (isProtoType) {
            _fields.add(PJProtocolCase.class.getSimpleName() + " " + name);
        } else {
            _fields.add(typeString + " " + name);
        }

        ST template = null;
        if (State.is(State.PROC_YIELDS)) {
            template = _stGroup.getInstanceOf("LocalDeclYieldingProc");
        } else {
            /*
             * This template also has flags for chanType, protoType, recType,
             * barrierType, etc. though those constructs are only for a yielding
             * proc. However, they are here to handle any declaration programmers
             * may put in their program though they don't use it (e.g. chan.read)
             * making the proc non-yielding.
             */
            template = _stGroup.getInstanceOf("LocalDeclNormalProc");
        }

        /*
         * We want to handle channel reads and invocations
         * differently as they will have extra code for
         * yields before the actual declaration/assignment.
         */
        Expression right = ld.var().init();

        if (State.is(State.PROC_YIELDS)
                && (right instanceof ChannelReadExpr || right instanceof Invocation || right instanceof NewArray)) {
            String assignment = (String) new Assignment(new NameExpr(ld.var().name()), right, Assignment.EQ)
                    .visit(this);
            return (T) assignment;
        } else {
            /*
             * Resolve type. 
             */
            Type t = ld.type();

            boolean isBarrierType = false;
            if (t instanceof PrimitiveType && t.isBarrierType()) {
                isBarrierType = true;
            }

            boolean isChanType = t.isChannelType();

            template.add("isChanType", isChanType);
            template.add("isProtoType", isProtoType);
            template.add("isRecType", isRecType);
            template.add("isBarrierType", isBarrierType);
            template.add("isTimerType", isTimerType);
            template.add("notInitialized", (ld.var().init() == null));

            String var = (String) ld.var().visit(this);
            template.add("var", var);
            template.add("typeStr", typeString);
            template.add("isForCtrl", State.is(State.FOR_LOOP_CONTROL));

            return (T) template.render();
        }

    }

    /**
     * IfStat TODO: We may want to change where we return either a String or a String[] to always return String[] even
     * if it only has one element.
     */
    public T visitIfStat(IfStat is) {
        Log.log(is.line + ": Visiting a IfStat");
        ST template = _stGroup.getInstanceOf("IfStat");

        List<String> binExprBlocks = new ArrayList<String>();
        
        State.set(State.IF_ELSE_PREDICATE, true);

        String expr = (String) is.expr().visit(this);

        if (this.binExprBlocks.size() > 0) {
            binExprBlocks.addAll(this.binExprBlocks);

            this.binExprBlocks.clear();
        }
        
        State.set(State.IF_ELSE_PREDICATE, false);
        
        Statement elsePart = is.elsepart();
        Object elsePartStr = null;
        Object thenPart = is.thenpart().visit(this);

        template.add("expr", expr);

        // May be one statement or multiple statements.
        if (thenPart instanceof String[]) {
            template.add("thenPart", (String[]) thenPart);
        } else {
            template.add("thenPart", (String) thenPart);
        }

        // May or may not be here!
        if (elsePart != null) {
            elsePartStr = elsePart.visit(this);

            if (elsePartStr instanceof String[]) {
                template.add("elsePart", (String[]) elsePartStr);
            } else {
                template.add("elsePart", (String) elsePartStr);
            }
        }
        
        template.add("binExprBlocks", binExprBlocks);

        return (T) template.render();
    }

    /**
     * Name
     */
    public T visitName(Name na) {
        Log.log(na.line + ": Visiting a Name (" + na.getname() + ")");

        String name = na.getname();
        String fieldName = null;

        if (_paramNameToFieldName != null) {
            fieldName = _paramNameToFieldName.get(name);
        }

        if (_localNameToFieldName != null && fieldName == null) {
            fieldName = _localNameToFieldName.get(name);
        }

        if (_protoNameToPrefixedName != null && fieldName == null) {
            fieldName = _protoNameToPrefixedName.get(name);
        }

        if (_recNameToPrefixedName != null && fieldName == null) {
            fieldName = _recNameToPrefixedName.get(name);
        }

        if (fieldName == null) {
            fieldName = name;
        }

        return (T) fieldName;
    }

    public T visitNamedType(NamedType nt) {
        Log.log(nt.line + ": Visiting NamedType (" + nt.name().getname() + ")");

        String pjTypeName = (String) nt.name().visit(this);

        if (nt.type() instanceof ExternType) {
            String externTypeName = (String) nt.type().visit(this);
            _pjTypeToExternType.put(pjTypeName, externTypeName);
            return null;
        }

        if (_pjTypeToExternType.containsKey(pjTypeName)) {
            return (T) _pjTypeToExternType.get(pjTypeName);
        } else {
            return (T) nt.name().visit(this);
        }
    }

    /**
     * NameExpr
     */
    public T visitNameExpr(NameExpr ne) {
        Log.log(ne.line + ": Visiting NameExpr (" + ne.name().getname() + ")");

        String name = (String) ne.name().visit(this);
        if (State.is(State.CLAIMSTAT)) {
            if (_chanNameToEndType.containsKey(name)) {
                if (_chanNameToEndType.get(name) == CHAN_READ_END) {
                    _isReadingChannelEnd = true;
                } else {
                    _isReadingChannelEnd = false;
                }
            }
        }

        return (T) name;
    }

    /**
     * NewArray
     */
    public T visitNewArray(NewArray ne) {
        Log.log(ne.line + ": Visiting a NewArray");

        return createNewArray(null, ne);
    }

    /**
     * NewMobile
     * 
     * TODO: cds do it.
     */

    /**
     * ParamDecl
     */
    public T visitParamDecl(ParamDecl pd) {
        Log.log(pd.line + ": Visiting a ParamDecl");

        ST template = _stGroup.getInstanceOf("ParamDecl");

        String type = (String) pd.type().visit(this);

        String name = pd.paramName().getname();
        String fieldName = Helper.convertToFieldName(name, true, this._varId++);
        pd.paramName().setName(name);

        if (_paramNameToFieldName != null) {
            _paramNameToFieldName.put(name, fieldName);
        }
        name = (String) pd.paramName().visit(this);

        if (pd.type() instanceof ChannelEndType
                || ((pd.type() instanceof ArrayType) && (((ArrayType) pd.type()).baseType() instanceof ChannelEndType))) {
            _chanNameToEndType.put(name, _chanEndType);
            _chanNameToBaseType.put(name, _chanBaseType);
        } else if (pd.type().isTimerType()) {
            _chanNameToBaseType.put(name, "long");
        }

        template.add("name", name);
        template.add("type", type);

        return (T) template.render();
    }

    /**
     * ParBlock
     */
    public T visitParBlock(ParBlock pb) {
        Log.log(pb.line + ": Visiting a ParBlock with stat size " + pb.stats().size());

        if (pb.stats().size() == 0) {
            return null;
        }

        ST parBlockTemplate = _stGroup.getInstanceOf("ParBlock");

        boolean oldParBlock = State.set(State.PAR_BLOCK, true);

        String nameHolder = null;
        nameHolder = _currParName;
        _currParName = "par" + ++this._parId;

        /*
        * Adding switch cases for Par resumption.
        */
        _switchCases.add(renderLookupSwitchCase(_jumpCnt));
        parBlockTemplate.add("jmp", _jumpCnt);
        _jumpCnt++;

        //BARRIER ------------------

        List<String> barriersTemp = null;
        Sequence<Expression> bs = pb.barriers();
        if (bs != null) {
            barriersTemp = new ArrayList<String>();
            barriersTemp.addAll(_barriers);
            _barriers.clear();
            _barriers.addAll(Arrays.asList((String[]) bs.visit(this)));
        }

        Sequence<Statement> se = pb.stats();
        String[] stats = new String[se.size()];
        
        for (int k = 0; k < se.size(); k++) {
            Object body = null;

            Statement st = se.child(k);

            if (st != null) {
                
                if (st instanceof ExprStat && ((ExprStat) st).expr() instanceof Invocation) {
                    
                    ExprStat es = (ExprStat) st;
                    Invocation inv = (Invocation) es.expr();
                    ProcTypeDecl pd = inv.targetProc;

                    if (Helper.isYieldingProc(pd)) {
                        stats[k] = (String) createInvocation(
                                null, 
                                null, 
                                inv, 
                                _barriers, 
                                false,
                                INV_WRAP_PAR
                            );
                    } else {
                        stats[k] = (String) (createAnonymousProcTypeDecl(st).visit(this)); 
                    }

                } else {
                    stats[k] = (String) (createAnonymousProcTypeDecl(st).visit(this));
                }
            }
        }

        parBlockTemplate.add("parCnt", stats.length);
        parBlockTemplate.add("stats", stats);
        parBlockTemplate.add("barriers", _barriers);
        parBlockTemplate.add("parName", _currParName);

        String object = parBlockTemplate.render();

        State.set(State.PAR_BLOCK, oldParBlock);
        _currParName = nameHolder;
        if (barriersTemp != null) {
            _barriers.clear();
            _barriers.addAll(barriersTemp);
        }

        return (T) object;
    }

    private ProcTypeDecl createAnonymousProcTypeDecl(Statement st) {
        return createAnonymousProcTypeDecl(new Block(new Sequence(st)));
    }

    private ProcTypeDecl createAnonymousProcTypeDecl(Block b) {
      //FIXME I don't think annotation is needed as anonymous is only used for processes
        Annotations a = new Annotations();
        a.add("yield", "true");

        return new ProcTypeDecl(
                    new Sequence(), 
                    null, 
                    new Name("Anonymous"), 
                    new Sequence(),
                    new Sequence(), 
                    a, 
                    b
                ); 
    }

    /**
     * PrimitiveLiteral
     */
    public T visitPrimitiveLiteral(PrimitiveLiteral li) {
        Log.log(li.line + ": Visiting a Primitive Literal");

        return (T) li.getText();
    }

    /**
     * PrimitiveType
     */
    public T visitPrimitiveType(PrimitiveType py) {
        Log.log(py.line + ": Visiting a Primitive Type");

        String typeString = py.toString();
        /*
         * Here we list all the primitive types 
         * that don't perfectly translate to Java
         * or do not have their own visitor to
         * return correct type.
         */
        if (py.isStringType()) {
            typeString = "String";
        } else if (py.isTimerType()) {
            typeString = PJTimer.class.getSimpleName();
        } else if (py.isBarrierType()) {
            typeString = PJBarrier.class.getSimpleName();
        }
        return (T) typeString;
    }

    /**
     * ProcTypeDecl
     */
    public T visitProcTypeDecl(ProcTypeDecl pd) {

        String currentProcName = this._currProcName;
        this._currProcName = (String) pd.name().visit(this);
        String templateProcName = prefixProcName(this._currProcName);

        boolean anonymous = false;
        if (_currProcName.equals("Anonymous")) {
            anonymous = true;
        }

        Log.log(pd.line + ": Visiting a ProcTypeDecl **(" + _currProcName + ")**");

        State.set(State.PROC_YIELDS, Helper.isYieldingProc(pd));

        ST template = null;
        String rendered = null;

        if (anonymous) {
            template = _stGroup.getInstanceOf("AnonymousProcess");

            /*
             * Backing up necessary global vars
             */
            List<String> holder = new ArrayList<String>();
            if (this._switchCases != null && this._switchCases.size() > 0) {
                holder.addAll(this._switchCases);
                this._switchCases = new ArrayList<String>();
            }

            String[] block = (String[]) pd.body().visit(this);

            template.add("body", block);
            template.add("lookupswitch", renderLookupSwitchTable(_switchCases));
            template.add("parName", _currParName);
            template.add("parfor", State.is(State.PARFOR));
            template.add("barriers", _barriers);

            rendered = template.render();

            _switchCases.clear();
            if (holder.size() > 0) {
                _switchCases.addAll(holder);
            }

        } else {

            if (State.is(State.PROC_YIELDS)) {
                template = _stGroup.getInstanceOf("ProcTypeDeclToProcess");
            } else {
                template = _stGroup.getInstanceOf("ProcTypeDeclToMethod");
            }

            /*
             * Initializing global var count for new class.
             */
            this._jumpCnt = 1;
            this._varId = 0;
            this._parId = 0;
            this._altId = 1;
            this._paramNameToFieldName = new HashMap<String, String>();
            this._localNameToFieldName = new HashMap<String, String>();
            this._fields = new ArrayList<String>();
            this._switchCases = new ArrayList<String>();

            State.set(State.FOREVER_LOOP, false);

            String returnType = (String) pd.returnType().visit(this);

            boolean oldFormals = State.set(State.PARAMS, true);
            String[] formals = (String[]) pd.formalParams().visit(this);
            State.set(State.PARAMS, oldFormals);

            String[] block = (String[]) pd.body().visit(this);

            Statement lastStat = null;
            if (pd.body().stats().size() > 0) {
                lastStat = pd.body().stats().child(pd.body().stats().size() - 1);
            }
            if (lastStat != null && lastStat instanceof ReturnStat) {
                template.add("retstatFound", true);
            }

            template.add("packageName", this._sourceFilename);
            template.add("returnType", returnType);
            template.add("name", templateProcName);
            if (formals.length != 0) {
                template.add("formals", formals);
            }
            if (_paramNameToFieldName.values().size() != 0) {
                template.add("formalNames", _paramNameToFieldName.values());
            }
            if (_fields.size() != 0) {
                template.add("globals", _fields.toArray(new String[_fields.size()]));
            }

            boolean isMain = "main".equals(_currProcName) && MAIN_SIGNATURE.equals(pd.signature());

            if (isMain && State.is(State.PROC_YIELDS)) {
                String qualifiedPkg = Helper.getQualifiedPkg(pd, this._sourceFilename);
                String qualifiedProc = qualifiedPkg + "." + templateProcName;
                ST mainTemplate = _stGroup.getInstanceOf("ProcTypeDeclToMain");
                mainTemplate.add("qualifiedProc", qualifiedProc);

                template.add("mainMethod", mainTemplate.render());
            }

            template.add("body", block);
            template.add("lookupswitch", renderLookupSwitchTable(_switchCases));
            template.add("foreverloop", State.is(State.FOREVER_LOOP));
            
            rendered = template.render();
        }

        /*
         * Resetting global vars
         */
        _currProcName = currentProcName;

        return (T) rendered;
    }

    /**
     * Protocol Literal
     */
    public T visitProtocolLiteral(ProtocolLiteral pl) {
        Log.log(pl.line + ": Visiting a ProtocolLiteral");
        ST template = _stGroup.getInstanceOf("ProtocolLiteral");

        String protocolName = (String) pl.name().visit(this);
        String tag = (String) pl.tag().visit(this);
        String tagName = _protoTagNameToPrefixedName.get(tag);
        String[] params = (String[]) pl.expressions().visit(this);

        template.add("protocolName", protocolName);
        template.add("tagName", tagName);
        template.add("params", params);
        return (T) template.render();
    }

    /**
     * Protocol Declaration
     */
    public T visitProtocolTypeDecl(ProtocolTypeDecl pd) {
        Log.log(pd.line + ": Visiting a ProtocolTypeDecl **(" + pd.name().getname() + ")**");

        ST template = _stGroup.getInstanceOf("ProtocolTypeDecl");

        prefixAndStoreProtocolName(pd.name());

        _currProtocolName = (String) pd.name().visit(this);

        //Visiting protocol cases.
        String[] cases = null;
        if (pd.body() != null) {
            cases = (String[]) pd.body().visit(this);
        }

        _currProtocolName = null;

        template.add("name", pd.name().getname());
        template.add("cases", cases);

        return (T) template.render();
    }

    /**
     * Protocol Case
     */
    public T visitProtocolCase(ProtocolCase pc) {
        Log.log(pc.line + ": Visiting a ProtocolCase");

        ST template = _stGroup.getInstanceOf("ProtocolCase");
        _recMemberNames = new ArrayList<String>();

        template.add("tag", pc.name().getname());
        //name: Protocol_A_a1
        prefixAndStoreProtocolTagName(pc.name());

        template.add("name", pc.name().getname());

        //recordMembers: int x; int y
        String[] rms = (String[]) pc.body().visit(this);
        template.add("recMems", rms);
        template.add("recMemNames", _recMemberNames);
        return (T) template.render();
    }

    public T visitRecordTypeDecl(RecordTypeDecl rt) {
        Log.log(rt.line + ": Visiting a RecordTypeDecl (" + rt.name().getname() + ")");
        ST template = _stGroup.getInstanceOf("RecordTypeDecl");

        _recMemberNames = new ArrayList<String>();

        prefixAndStoreRecordName(rt.name());

        List<String> rms = new ArrayList<String>();
        rms.addAll(Arrays.asList((String[]) rt.body().visit(this)));

        for (Name name : rt.extend()) {
            Type t = (Type) _topLevelDecls.get((String) name.visit(this));
            if (t instanceof RecordTypeDecl) {
                RecordTypeDecl tt = (RecordTypeDecl) t;
                rms.addAll(Arrays.asList((String[]) tt.body().visit(this)));
            }
        }

        template.add("name", rt.name().visit(this));
        template.add("recMems", rms);
        template.add("recMemNames", _recMemberNames);

        return (T) template.render();
    }

    public T visitRecordMember(RecordMember rm) {
        Log.log(rm.line + ": Visiting a RecordMember (" + rm.type().typeName() + " " + rm.name().getname() + ")");

        ST template = _stGroup.getInstanceOf("RecordMember");

        _recMemberNames.add((String) rm.name().visit(this));

        template.add("type", rm.type().visit(this));
        template.add("name", rm.name().visit(this));
        return (T) template.render();
    }

    public T visitRecordAccess(RecordAccess ra) {
        Log.log(ra.line + ": Visiting a RecordAccess = " + ra.toString());

        ST template = _stGroup.getInstanceOf("RecordAccess");

        Type tType = ra.record().type;

        if (tType.isRecordType()) {
            Log.log("found a record type");
        } else if (tType.isProtocolType()) {
            ProtocolTypeDecl pt = (ProtocolTypeDecl) ra.record().type;
            String caseName = _protoTagNameToPrefixedName.get(_protoNameToProtoTagSwitchedOn.get(pt.name().getname()));
            String protocolName = _protoTagNameToProtoName.get(caseName);

            template.add("protocolName", protocolName);
            template.add("caseName", caseName);
            template.add("record", ra.record().visit(this));
            template.add("field", ra.field().visit(this));
        }

        return (T) template.render();
    }

    public T visitRecordLiteral(RecordLiteral rl) {
        Log.log(rl.line + ": Visiting a RecordLiteral");

        ST template = _stGroup.getInstanceOf("RecordLiteral");

        String name = (String) rl.name().visit(this);
        String[] params = (String[]) rl.members().visit(this);

        template.add("name", name);
        template.add("params", params);

        return (T) template.render();
    }

    /**
     * ReturnStat
     */
    public T visitReturnStat(ReturnStat rs) {
        Log.log(rs.line + ": Visiting a ReturnStat");

        ST template = _stGroup.getInstanceOf("ReturnStat");
        Expression expr = rs.expr();
        String exprStr = "";

        // Can return null so we must check for this!
        if (expr != null) {
            State.set(State.RETURN_STAT, true);
            exprStr = (String) expr.visit(this);
            State.set(State.RETURN_STAT, false);
            template.add("expr", exprStr);
        }
        template.add("procYields", State.is(State.PROC_YIELDS));

        return (T) template.render();
    }

    /**
     * Sequence
     */
    public T visitSequence(Sequence se) {
        Log.log(se.line + ": Visiting a Sequence");
        List<String> children = new ArrayList<String>();
        String[] stats = null;

        for (int i = 0; i < se.size(); i++) {
            if (se.child(i) != null) {
                stats = getStatements(se.child(i));
                if (stats != null && stats.length != 0) {
                    children.addAll(Arrays.asList(stats));
                }
            }
        }

        String[] returnArray = new String[children.size()];
        returnArray = children.toArray(returnArray);

        return (T) returnArray;
    }

    /**
     * SwitchGroup
     */
    public T visitSwitchGroup(SwitchGroup sg) {
        Log.log(sg.line + ": Visiting a SwitchGroup");

        ST template = _stGroup.getInstanceOf("SwitchGroup");
        String[] labels = (String[]) sg.labels().visit(this);
        String[] stmts = (String[]) sg.statements().visit(this);

        template.add("labels", labels);
        template.add("stmts", stmts);

        return (T) template.render();
    }

    /**
     * SwitchLabel
     */
    public T visitSwitchLabel(SwitchLabel sl) {
        Log.log(sl.line + ": Visiting a SwitchLabel");

        ST template = _stGroup.getInstanceOf("SwitchLabel");
        boolean isDefault = sl.isDefault();

        if (isDefault == false) {
            String constExpr = (String) sl.expr().visit(this);
            if (State.is(State.PROTOCOL_EXPR)) {
                constExpr = "\"" + constExpr + "\"";
            }
            template.add("constExpr", constExpr);
        } else {
            template.add("defaultExpr", "default");
        }

        return (T) template.render();
    }

    /**
     * SwitchStat
     */
    public T visitSwitchStat(SwitchStat st) {
        Log.log(st.line + ": Visiting a SwitchStat");

        ST template = _stGroup.getInstanceOf("SwitchStat");

        boolean oldVal = State.set(State.PROTOCOL_EXPR, st.expr().type.isProtocolType());

        String expr = (String) st.expr().visit(this);

        List<String> switchGroups = new ArrayList<String>();
        if (State.is(State.PROTOCOL_EXPR)) {

            ProtocolTypeDecl pt = (ProtocolTypeDecl) st.expr().type;

            for (SwitchGroup sg : st.switchBlocks()) {
                //FIXME why am I just visiting one label? Can there not be more than one?
                SwitchLabel sl = sg.labels().child(0);

                _protoNameToProtoTagSwitchedOn.put(pt.name().getname(), ((NameExpr) sl.expr()).name().getname());

                String label = (String) sl.visit(this);

                //This can have nested switchstat with protocol expression
                String[] stmts = (String[]) sg.statements().visit(this);

                ST template1 = _stGroup.getInstanceOf("SwitchGroup");
                template1.add("labels", label);
                template1.add("stmts", stmts);

                switchGroups.add(template1.render());

            }
        } else {
            switchGroups = Arrays.asList((String[]) st.switchBlocks().visit(this));
        }

        template.add("isProtocolExpr", State.is(State.PROTOCOL_EXPR));
        template.add("expr", expr);
        template.add("switchGroups", switchGroups);

        String rendered = template.render();

        State.set(State.PROTOCOL_EXPR, oldVal);

        return (T) rendered;
    }

    public T visitSyncStat(SyncStat st) {
        Log.log(st.line + ": Visiting a SyncStat");
        ST template = _stGroup.getInstanceOf("SyncStat");

        template.add("barrier", st.barrier().visit(this));
        template.add("jmp", _jumpCnt);

        _switchCases.add(renderLookupSwitchCase(_jumpCnt));
        _jumpCnt++;

        return (T) template.render();
    }

    /**
     * Ternary
     */
    public T visitTernary(Ternary te) {
        Log.log(te.line + ": Visiting a Ternary");

        ST template = _stGroup.getInstanceOf("Ternary");
        String expr = (String) te.expr().visit(this);
        String trueBranch = (String) te.trueBranch().visit(this);
        String falseBranch = (String) te.falseBranch().visit(this);

        template.add("expr", expr);
        template.add("trueBranch", trueBranch);
        template.add("falseBranch", falseBranch);

        return (T) template.render();
    }

    /**
     * TimeoutStat
     */
    public T visitTimeoutStat(TimeoutStat ts) {
        Log.log(ts.line + ": Visiting a TimeoutStat");
        ST template = _stGroup.getInstanceOf("TimeoutStat");

        template.add("alt", (State.is(State.ALT) && State.is(State.ALT_GUARD)));
        template.add("name", ts.timer().visit(this));
        template.add("delay", ts.delay().visit(this));
        template.add("jmp", _jumpCnt);

        _switchCases.add(renderLookupSwitchCase(_jumpCnt));
        _jumpCnt++;

        return (T) template.render();
    }

    /**
     * UnaryPostExpr
     */
    public T visitUnaryPostExpr(UnaryPostExpr up) {
        Log.log(up.line + ": Visiting a UnaryPostExpr");

        ST template = _stGroup.getInstanceOf("UnaryPostExpr");
        String expr = (String) up.expr().visit(this);
        String op = up.opString();

        template.add("expr", expr);
        template.add("op", op);
        
        boolean noDelim = State.is(State.FOR_LOOP_CONTROL)
                            || State.is(State.CHAN_WRITE_VALUE)
                            || State.is(State.INV_ARG)
                            || State.is(State.WHILE_EXPR)
                            || State.is(State.RETURN_STAT)
                            || State.is(State.IF_ELSE_PREDICATE);

        template.add("noDelim", noDelim);

        return (T) template.render();
    }

    /**
     * UnaryPreExpr
     */
    public T visitUnaryPreExpr(UnaryPreExpr up) {
        Log.log(up.line + ": Visiting a UnaryPreExpr");

        ST template = _stGroup.getInstanceOf("UnaryPreExpr");
        String expr = (String) up.expr().visit(this);
        String op = up.opString();

        template.add("expr", expr);
        template.add("op", op);
        
        boolean noDelim = State.is(State.FOR_LOOP_CONTROL) 
                || State.is(State.CHAN_WRITE_VALUE)
                || State.is(State.INV_ARG)
                || State.is(State.WHILE_EXPR)
                || State.is(State.RETURN_STAT)
                || State.is(State.IF_ELSE_PREDICATE);

        template.add("noDelim", noDelim);

        return (T) template.render();
    }

    /**
     * Var
     */
    public T visitVar(Var va) {
        Log.log(va.line + ": Visiting a Var: " + va.name().getname());

        ST template = _stGroup.getInstanceOf("Var");
        String name = (String) va.name().visit(this);
        String exprStr = "";
        Expression expr = va.init();

        /*
         * This is to get the var name inside
         * code generated for channel read.
         */
        if (expr instanceof ChannelReadExpr) {
            /*
             * TODO 03.25.2016 why is this commented?
             * remove this if not needed coz seems like this might
             * ignore channel read expr.
             */
            //      ((ChannelReadExpr)expr).varname = name;
        } else {
            template.add("name", name);
        }

        // Expr may be null if the variable is not initialized to anything!
        if (expr != null) {
            exprStr = (String) expr.visit(this);
            template.add("init", exprStr);
        }

        template.add("isForCtrl", State.is(State.FOR_LOOP_CONTROL));

        return (T) template.render();
    }

    /**
     * WhileStat
     */
    public T visitWhileStat(WhileStat ws) {
        Log.log(ws.line + ": Visiting a WhileStat");

        ST template = _stGroup.getInstanceOf("WhileStat");

        updateForeverLoop(ws.foreverLoop);

        List<String> exprLst = new ArrayList<String>();
        List<String> exprBlocks = new ArrayList<String>();
        String fieldNameTemp = null;

        Expression e = ws.expr();

        if (e != null) {
            if (e instanceof ChannelReadExpr) {

                ChannelReadExpr cr = (ChannelReadExpr) e;
                String typeString = _chanNameToBaseType.get(cr.channel().visit(this));
                fieldNameTemp = Helper.convertToFieldName("temp", false, this._varId++);
                _fields.add(typeString + " " + fieldNameTemp);
                String chanReadBlock = (String) createChannelReadExpr(fieldNameTemp, "=", (ChannelReadExpr) e);

                exprBlocks.add(chanReadBlock);
                exprLst.add(fieldNameTemp);

            } else if (e instanceof Invocation) {

                String typeString = ((Invocation) e).targetProc.returnType().typeName();
                fieldNameTemp = Helper.convertToFieldName("temp", false, this._varId++);
                _fields.add(typeString + " " + fieldNameTemp);
                String invocationBlock = (String) createInvocation(fieldNameTemp, "=", (Invocation) e, true);

                exprBlocks.add(invocationBlock);
                exprLst.add(fieldNameTemp);

            } else if (e instanceof BinaryExpr) {
                String typeString = (String) ((BinaryExpr) e).type.visit(this);
                fieldNameTemp = Helper.convertToFieldName("temp", false, this._varId++);
                _fields.add(typeString + " " + fieldNameTemp);

                String binaryExprBlock = (String) createBinaryExpr(fieldNameTemp, "=", (BinaryExpr) e);

                exprBlocks.add(binaryExprBlock);
                exprLst.add(fieldNameTemp);
            } else {

                State.set(State.WHILE_EXPR, true);

                String name = (String) e.visit(this);
                
                State.set(State.WHILE_EXPR, true);

                exprLst.add(name);

            } 
        }

        Object stats = ws.stat().visit(this);

        if (stats instanceof String[]) {
            template.add("stat", (String[]) stats);
        } else {
            template.add("stat", (String) stats);
        }

        template.add("exprBlocks", exprBlocks);
        template.add("exprLst", exprLst);

        return (T) template.render();
    }

    /**
     * Getting the basetype for Channel<basetype>
     */
    private String getChannelBaseType(Type t) {
        String basetype = null;
        if (t.isNamedType()) {
            NamedType tt = (NamedType) t;

            if (isProtocolType(tt)) {
                basetype = PJProtocolCase.class.getSimpleName();
            } else {
                basetype = (String) tt.visit(this);
            }
        } else if (t.isChannelEndType()) {
            //TODO: does this need to be handled? as channels can be passed through channels.
            System.out.println("ChannelEndType for Channel baseType not yet handled!!");
        } else if (t.isPrimitiveType()) {
            basetype = Helper.getWrapperType(t);
        }

        return basetype;
    }

    private String renderLookupSwitchTable(List<String> cases) {
        ST switchTemplate = _stGroup.getInstanceOf("LookupSwitchTable");
        switchTemplate.add("cases", cases);
        return switchTemplate.render();
    }

    /**
     * Switch cases for runLabel switch table.
     */
    private String renderLookupSwitchCase(int jmp) {
        ST template = null;
        template = _stGroup.getInstanceOf("LookupSwitchCase");
        template.add("caseNum", jmp);
        return template.render();
    }

    private void prefixAndStoreProtocolName(Name name) {
        String modified = "Protocol_" + name.getname();
        _protoNameToPrefixedName.put(name.getname(), modified);
        name.setName(modified);
    }

    private void prefixAndStoreProtocolTagName(Name name) {
        String modified = _currProtocolName + "_" + name.getname();
        _protoTagNameToPrefixedName.put(name.getname(), modified);
        _protoTagNameToProtoName.put(modified, _currProtocolName + "");
        name.setName(modified);
    }

    private void prefixAndStoreRecordName(Name name) {
        String modified = "Record_" + name.getname();
        _recNameToPrefixedName.put(name.getname(), modified);
        name.setName(modified);
    }

    private String prefixProcName(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("proc");
        sb.append(name.substring(0, 1).toUpperCase());
        sb.append(name.substring(1));
        return sb.toString();
    }

    private void globalizeAndStoreLocalDeclName(Name name) {
        String fieldName = Helper.convertToFieldName(name.getname(), false, this._varId++);
        _localNameToFieldName.put(name.getname(), fieldName);
        name.setName(fieldName);
    }
    
    private void updateForeverLoop(boolean bVal) {
        if (bVal && !State.is(State.FOREVER_LOOP)) {
            State.set(State.FOREVER_LOOP, bVal);
        }
    }

    private String[] getStatements(AST stat) {
        String[] stats = null;

        if (stat instanceof Block) {
            Block b = (Block) stat;
            stats = (String[]) b.visit(this);
        } else {
            stats = new String[1];
            stats[0] = (String) stat.visit(this);
        }

        return stats;
    }

    private boolean isProtocolType(NamedType nt) {
        return _protoNameToPrefixedName.containsKey(nt.name().getname());
    }

    private boolean isRecordType(NamedType nt) {
        return _recNameToPrefixedName.containsKey(nt.name().getname());
    }

}