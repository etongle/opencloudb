/*
 * Copyright 2012-2015 org.opencloudb.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * (created at 2011-7-21)
 */
package org.opencloudb.route.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opencloudb.config.model.TableConfig;
import org.opencloudb.paser.ast.ASTNode;
import org.opencloudb.paser.ast.ddl.ColumnDefinition;
import org.opencloudb.paser.ast.ddl.TableOptions;
import org.opencloudb.paser.ast.ddl.datatype.DataType;
import org.opencloudb.paser.ast.ddl.index.IndexColumnName;
import org.opencloudb.paser.ast.ddl.index.IndexOption;
import org.opencloudb.paser.ast.expression.BinaryOperatorExpression;
import org.opencloudb.paser.ast.expression.Expression;
import org.opencloudb.paser.ast.expression.PolyadicOperatorExpression;
import org.opencloudb.paser.ast.expression.ReplacableExpression;
import org.opencloudb.paser.ast.expression.UnaryOperatorExpression;
import org.opencloudb.paser.ast.expression.comparison.BetweenAndExpression;
import org.opencloudb.paser.ast.expression.comparison.ComparisionEqualsExpression;
import org.opencloudb.paser.ast.expression.comparison.ComparisionIsExpression;
import org.opencloudb.paser.ast.expression.comparison.ComparisionNullSafeEqualsExpression;
import org.opencloudb.paser.ast.expression.comparison.InExpression;
import org.opencloudb.paser.ast.expression.function.FunctionExpression;
import org.opencloudb.paser.ast.expression.function.cast.Cast;
import org.opencloudb.paser.ast.expression.function.cast.Convert;
import org.opencloudb.paser.ast.expression.function.datetime.Extract;
import org.opencloudb.paser.ast.expression.function.datetime.GetFormat;
import org.opencloudb.paser.ast.expression.function.datetime.Timestampadd;
import org.opencloudb.paser.ast.expression.function.datetime.Timestampdiff;
import org.opencloudb.paser.ast.expression.function.groupby.Avg;
import org.opencloudb.paser.ast.expression.function.groupby.Count;
import org.opencloudb.paser.ast.expression.function.groupby.GroupConcat;
import org.opencloudb.paser.ast.expression.function.groupby.Max;
import org.opencloudb.paser.ast.expression.function.groupby.Min;
import org.opencloudb.paser.ast.expression.function.groupby.Sum;
import org.opencloudb.paser.ast.expression.function.literal.IntervalPrimary;
import org.opencloudb.paser.ast.expression.function.literal.LiteralBitField;
import org.opencloudb.paser.ast.expression.function.literal.LiteralBoolean;
import org.opencloudb.paser.ast.expression.function.literal.LiteralHexadecimal;
import org.opencloudb.paser.ast.expression.function.literal.LiteralNull;
import org.opencloudb.paser.ast.expression.function.literal.LiteralNumber;
import org.opencloudb.paser.ast.expression.function.literal.LiteralString;
import org.opencloudb.paser.ast.expression.function.string.Char;
import org.opencloudb.paser.ast.expression.function.string.LikeExpression;
import org.opencloudb.paser.ast.expression.function.string.Trim;
import org.opencloudb.paser.ast.expression.function.type.CollateExpression;
import org.opencloudb.paser.ast.expression.logical.LogicalAndExpression;
import org.opencloudb.paser.ast.expression.logical.LogicalOrExpression;
import org.opencloudb.paser.ast.expression.misc.InExpressionList;
import org.opencloudb.paser.ast.expression.misc.QueryExpression;
import org.opencloudb.paser.ast.expression.misc.UserExpression;
import org.opencloudb.paser.ast.expression.primary.CaseWhenOperatorExpression;
import org.opencloudb.paser.ast.expression.primary.DefaultValue;
import org.opencloudb.paser.ast.expression.primary.ExistsPrimary;
import org.opencloudb.paser.ast.expression.primary.Identifier;
import org.opencloudb.paser.ast.expression.primary.MatchExpression;
import org.opencloudb.paser.ast.expression.primary.ParamMarker;
import org.opencloudb.paser.ast.expression.primary.PlaceHolder;
import org.opencloudb.paser.ast.expression.primary.RowExpression;
import org.opencloudb.paser.ast.expression.primary.SysVarPrimary;
import org.opencloudb.paser.ast.expression.primary.UsrDefVarPrimary;
import org.opencloudb.paser.ast.expression.primary.Wildcard;
import org.opencloudb.paser.ast.fragment.GroupBy;
import org.opencloudb.paser.ast.fragment.Limit;
import org.opencloudb.paser.ast.fragment.OrderBy;
import org.opencloudb.paser.ast.fragment.SortOrder;
import org.opencloudb.paser.ast.stmt.dal.DALSetCharacterSetStatement;
import org.opencloudb.paser.ast.stmt.dal.DALSetNamesStatement;
import org.opencloudb.paser.ast.stmt.dal.DALSetStatement;
import org.opencloudb.paser.ast.stmt.dal.ShowAuthors;
import org.opencloudb.paser.ast.stmt.dal.ShowBinLogEvent;
import org.opencloudb.paser.ast.stmt.dal.ShowBinaryLog;
import org.opencloudb.paser.ast.stmt.dal.ShowCharaterSet;
import org.opencloudb.paser.ast.stmt.dal.ShowCollation;
import org.opencloudb.paser.ast.stmt.dal.ShowColumns;
import org.opencloudb.paser.ast.stmt.dal.ShowContributors;
import org.opencloudb.paser.ast.stmt.dal.ShowCreate;
import org.opencloudb.paser.ast.stmt.dal.ShowDatabases;
import org.opencloudb.paser.ast.stmt.dal.ShowEngine;
import org.opencloudb.paser.ast.stmt.dal.ShowEngines;
import org.opencloudb.paser.ast.stmt.dal.ShowErrors;
import org.opencloudb.paser.ast.stmt.dal.ShowEvents;
import org.opencloudb.paser.ast.stmt.dal.ShowFunctionCode;
import org.opencloudb.paser.ast.stmt.dal.ShowFunctionStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowGrants;
import org.opencloudb.paser.ast.stmt.dal.ShowIndex;
import org.opencloudb.paser.ast.stmt.dal.ShowMasterStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowOpenTables;
import org.opencloudb.paser.ast.stmt.dal.ShowPlugins;
import org.opencloudb.paser.ast.stmt.dal.ShowPrivileges;
import org.opencloudb.paser.ast.stmt.dal.ShowProcedureCode;
import org.opencloudb.paser.ast.stmt.dal.ShowProcedureStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowProcesslist;
import org.opencloudb.paser.ast.stmt.dal.ShowProfile;
import org.opencloudb.paser.ast.stmt.dal.ShowProfiles;
import org.opencloudb.paser.ast.stmt.dal.ShowSlaveHosts;
import org.opencloudb.paser.ast.stmt.dal.ShowSlaveStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowTableStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowTables;
import org.opencloudb.paser.ast.stmt.dal.ShowTriggers;
import org.opencloudb.paser.ast.stmt.dal.ShowVariables;
import org.opencloudb.paser.ast.stmt.dal.ShowWarnings;
import org.opencloudb.paser.ast.stmt.ddl.DDLAlterTableStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLAlterTableStatement.AlterSpecification;
import org.opencloudb.paser.ast.stmt.ddl.DDLCreateIndexStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLCreateTableStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLDropIndexStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLDropTableStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLRenameTableStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLTruncateStatement;
import org.opencloudb.paser.ast.stmt.ddl.DescTableStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLCallStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLDeleteStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLInsertReplaceStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLInsertStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLReplaceStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLSelectStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLSelectUnionStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLUpdateStatement;
import org.opencloudb.paser.ast.stmt.extension.ExtDDLCreatePolicy;
import org.opencloudb.paser.ast.stmt.extension.ExtDDLDropPolicy;
import org.opencloudb.paser.ast.stmt.mts.MTSReleaseStatement;
import org.opencloudb.paser.ast.stmt.mts.MTSRollbackStatement;
import org.opencloudb.paser.ast.stmt.mts.MTSSavepointStatement;
import org.opencloudb.paser.ast.stmt.mts.MTSSetTransactionStatement;
import org.opencloudb.paser.ast.tableref.Dual;
import org.opencloudb.paser.ast.tableref.IndexHint;
import org.opencloudb.paser.ast.tableref.InnerJoin;
import org.opencloudb.paser.ast.tableref.NaturalJoin;
import org.opencloudb.paser.ast.tableref.OuterJoin;
import org.opencloudb.paser.ast.tableref.StraightJoin;
import org.opencloudb.paser.ast.tableref.SubqueryFactor;
import org.opencloudb.paser.ast.tableref.TableRefFactor;
import org.opencloudb.paser.ast.tableref.TableReference;
import org.opencloudb.paser.ast.tableref.TableReferences;
import org.opencloudb.paser.util.ExprEvalUtils;
import org.opencloudb.paser.util.Pair;
import org.opencloudb.paser.visitor.SQLASTVisitor;
import org.opencloudb.util.SmallSet;

/**
 * @author mycat
 */
public final class PartitionKeyVisitor implements SQLASTVisitor {

    private static final Set<Class<? extends Expression>> VERDICT_PASS_THROUGH_WHERE =
            new HashSet<Class<? extends Expression>>(6);
    private static final Set<Class<? extends Expression>> GROUP_FUNC_PASS_THROUGH_SELECT =
            new HashSet<Class<? extends Expression>>(5);
    private static final Set<Class<? extends Expression>> PARTITION_OPERAND_SINGLE =
            new HashSet<Class<? extends Expression>>(3);
    static {
        VERDICT_PASS_THROUGH_WHERE.add(LogicalAndExpression.class);
        VERDICT_PASS_THROUGH_WHERE.add(LogicalOrExpression.class);
        VERDICT_PASS_THROUGH_WHERE.add(BetweenAndExpression.class);
        VERDICT_PASS_THROUGH_WHERE.add(InExpression.class);
        VERDICT_PASS_THROUGH_WHERE.add(ComparisionNullSafeEqualsExpression.class);
        VERDICT_PASS_THROUGH_WHERE.add(ComparisionEqualsExpression.class);
        GROUP_FUNC_PASS_THROUGH_SELECT.add(Count.class);
        GROUP_FUNC_PASS_THROUGH_SELECT.add(Sum.class);
        GROUP_FUNC_PASS_THROUGH_SELECT.add(Min.class);
        GROUP_FUNC_PASS_THROUGH_SELECT.add(Max.class);
        GROUP_FUNC_PASS_THROUGH_SELECT.add(Wildcard.class);
        PARTITION_OPERAND_SINGLE.add(BetweenAndExpression.class);
        PARTITION_OPERAND_SINGLE.add(ComparisionNullSafeEqualsExpression.class);
        PARTITION_OPERAND_SINGLE.add(ComparisionEqualsExpression.class);
    }

    private static boolean isVerdictPassthroughWhere(Expression node) {
        if (node == null) return false;
        return VERDICT_PASS_THROUGH_WHERE.contains(node.getClass());
    }

    private static boolean isGroupFuncPassthroughSelect(Expression node) {
        if (node == null) return false;
        return GROUP_FUNC_PASS_THROUGH_SELECT.contains(node.getClass());
    }

    public static boolean isPartitionKeyOperandSingle(Expression expr, ASTNode parent) {
        return parent == null
               && expr instanceof ReplacableExpression
               && PARTITION_OPERAND_SINGLE.contains(expr.getClass());
    }

    public static boolean isPartitionKeyOperandIn(Expression expr, ASTNode parent) {
        return expr != null && parent instanceof InExpression;
    }

    // ---output------------------------------------------------------------------
    public static final int GROUP_CANCEL = -1;
    public static final int GROUP_NON = 0;
    public static final int GROUP_SUM = 1;
    public static final int GROUP_MIN = 2;
    public static final int GROUP_MAX = 3;
    private boolean dual = false;
    private int groupFuncType = GROUP_NON;
    private long limitSize = -1L;
    private long limitOffset = -1L;
    private boolean tableMetaRead;
    private boolean rewriteField = false;
    private boolean schemaTrimmed = false;
    private boolean customedSchema = false;
    /** {tableNameUp -&gt; {columnNameUp -&gt; columnValues}}, obj[] never null */
    private Map<String, Map<String, List<Object>>> columnValue = new HashMap<String, Map<String, List<Object>>>(2, 1);
    /** {table -&gt; {column -&gt; {value -&gt; [(expr,parentExpr)]}}} */
    private Map<String, Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>>> columnValueIndex;
    private Map<String, String> tableAlias = new HashMap<String, String>(4, 1);

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public boolean isDual() {
        return dual;
    }

    public boolean isCustomedSchema() {
        return customedSchema;
    }

    public boolean isTableMetaRead() {
        return tableMetaRead;
    }

    public boolean isNeedRewriteField() {
        return rewriteField;
    }

    /**
     * @return null for statement not table meta read
     */
    public String[] getMetaReadTable() {
        if (isTableMetaRead()) {
            Set<String> tables = columnValue.keySet();
            if (tables == null || tables.isEmpty()) {
                return EMPTY_STRING_ARRAY;
            }
            String[] array = new String[tables.size()];
            Iterator<String> iter = tables.iterator();
            for (int i = 0; i < array.length; ++i) {
                array[i] = iter.next();
            }
            return array;
        }
        return null;
    }

    public Map<String, String> getTableAlias() {
        return tableAlias;
    }

    /**
     * @return -1 for no limit
     */
    public long getLimitOffset() {
        return limitOffset;
    }

    /**
     * @return -1 for no limit
     */
    public long getLimitSize() {
        return limitSize;
    }

    /**
     * @return {@link #GROUP_NON} or {@link #GROUP_SUM}or {@link #GROUP_MIN}or
     *         {@link #GROUP_MAX}
     */
    public int getGroupFuncType() {
        return groupFuncType;
    }

    public boolean isSchemaTrimmed() {
        return schemaTrimmed;
    }

    /** @return never null */
    public Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>> getColumnIndex(String tableNameUp) {
        if (columnValueIndex == null) return Collections.emptyMap();
        Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>> index = columnValueIndex.get(tableNameUp);
        if (index == null || index.isEmpty()) return Collections.emptyMap();
        return index;
    }

    /**
     * @return <code>table -&gt; null</code> is possible
     */
    public Map<String, Map<String, List<Object>>> getColumnValue() {
        return columnValue;
    }

    private void addTable(String tableNameUp) {
        addTable(tableNameUp, 2);
    }

    /**
     * @param initColumnMapSize 0 for emptyMap
     */
    private void addTable(String tableNameUp, int initColumnMapSize) {
        if (columnValue.containsKey(tableNameUp)) return;
        Map<String, List<Object>> colMap;
        if (initColumnMapSize > 0) {
            colMap = new HashMap<String, List<Object>>(initColumnMapSize, 1);
        } else {
            colMap = Collections.emptyMap();
        }
        columnValue.put(tableNameUp, colMap);
    }

    private void addColumnValueIndex(String table, String column, Object value, Expression expr, ASTNode parent) {
        Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>> colMap = ensureColumnValueIndexByTable(table);
        Map<Object, Set<Pair<Expression, ASTNode>>> valMap = ensureColumnValueIndexObjMap(colMap, column);
        addIntoColumnValueIndex(valMap, value, expr, parent);
    }

    private void addIntoColumnValueIndex(Map<Object, Set<Pair<Expression, ASTNode>>> valMap,
                                         Object value,
                                         Expression expr,
                                         ASTNode parent) {
        Set<Pair<Expression, ASTNode>> exprSet = valMap.get(value);
        if (exprSet == null) {
            // exprSet = new HashSet<Pair<Expression, ASTNode>>(2, 1);
            exprSet = new SmallSet<Pair<Expression, ASTNode>>();
            valMap.put(value, exprSet);
        }
        Pair<Expression, ASTNode> pair = new Pair<Expression, ASTNode>(expr, parent);
        exprSet.add(pair);
    }

    private Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>> ensureColumnValueIndexByTable(String table) {
        if (columnValueIndex == null) {
            columnValueIndex = new HashMap<String, Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>>>(2, 1);
        }
        Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>> colMap = columnValueIndex.get(table);
        if (colMap == null) {
            colMap = new HashMap<String, Map<Object, Set<Pair<Expression, ASTNode>>>>(2, 1);
            columnValueIndex.put(table, colMap);
        }
        return colMap;
    }

    private Map<Object, Set<Pair<Expression, ASTNode>>> ensureColumnValueIndexObjMap(Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>> colMap,
                                                                                     String column) {
        Map<Object, Set<Pair<Expression, ASTNode>>> valMap = colMap.get(column);
        if (valMap == null) {
            valMap = new HashMap<Object, Set<Pair<Expression, ASTNode>>>(2, 1);
            colMap.put(column, valMap);
        }
        return valMap;
    }

    private void addColumnValue(String tableNameUp, String columnNameUp, Object value, Expression expr, ASTNode parent) {
        Map<String, List<Object>> colVals = ensureColumnValueByTable(tableNameUp);
        ensureColumnValueList(colVals, columnNameUp).add(value);
        addColumnValueIndex(tableNameUp, columnNameUp, value, expr, parent);
    }

    private Map<String, List<Object>> ensureColumnValueByTable(String tableNameUp) {
        Map<String, List<Object>> colVals = columnValue.get(tableNameUp);
        if (colVals == null) {
            colVals = new HashMap<String, List<Object>>(2, 1);
            columnValue.put(tableNameUp, colVals);
        }
        return colVals;
    }

    private List<Object> ensureColumnValueList(Map<String, List<Object>> columnValue, String column) {
        List<Object> list = columnValue.get(column);
        if (list == null) {
            list = new LinkedList<Object>();
            columnValue.put(column, list);
        }
        return list;
    }

    // ---temp
    // state------------------------------------------------------------------
    private final Map<Object, Object> evaluationParameter = Collections.emptyMap();
    private final Map<String, TableConfig> tablesRuleConfig;
    private boolean verdictColumn = true;
    private int idLevel = 2;
    private boolean verdictGroupFunc = true;
    private String trimSchema;

    public PartitionKeyVisitor(Map<String, TableConfig> tables) {
        if (tables == null || tables.isEmpty()) {
            tables = Collections.emptyMap();
        }
        this.tablesRuleConfig = tables;
    }

    public PartitionKeyVisitor setTrimSchema(String trimSchema) {
        if (trimSchema != null) {
            this.trimSchema = trimSchema.toUpperCase();
        }
        return this;
    }

    private boolean isRuledColumn(String tableNameUp, String columnNameUp) {
        if (tableNameUp == null) {
            return false;
        }
        TableConfig config = tablesRuleConfig.get(tableNameUp);
        if (config != null) {
            return config.existsColumn(columnNameUp);
        }
        return false;
    }

    private void visitChild(int idLevel, boolean verdictColumn, boolean verdictGroupFunc, ASTNode... nodes) {
        if (nodes == null || nodes.length <= 0) return;
        int oldLevel = this.idLevel;
        boolean oldVerdict = this.verdictColumn;
        boolean oldverdictGroupFunc = this.verdictGroupFunc;
        this.idLevel = idLevel;
        this.verdictColumn = verdictColumn;
        this.verdictGroupFunc = verdictGroupFunc;
        try {
            for (ASTNode node : nodes) {
                if (node != null) node.accept(this);
            }
        } finally {
            this.verdictColumn = oldVerdict;
            this.idLevel = oldLevel;
            this.verdictGroupFunc = oldverdictGroupFunc;
        }
    }

    private void visitChild(int idLevel, boolean verdictColumn, boolean verdictGroupFunc, List<? extends ASTNode> nodes) {
        if (nodes == null || nodes.isEmpty()) return;
        int oldLevel = this.idLevel;
        boolean oldVerdict = this.verdictColumn;
        boolean oldverdictGroupFunc = this.verdictGroupFunc;
        this.idLevel = idLevel;
        this.verdictColumn = verdictColumn;
        this.verdictGroupFunc = verdictGroupFunc;
        try {
            for (ASTNode node : nodes) {
                if (node != null) node.accept(this);
            }
        } finally {
            this.verdictColumn = oldVerdict;
            this.idLevel = oldLevel;
            this.verdictGroupFunc = oldverdictGroupFunc;
        }
    }

    // --------------------------------------------------------------------------------
    private void limit(Limit limit) {
        if (limit != null) {
            Object ls = limit.getSize();
            if (ls instanceof Expression) ls = ((Expression) ls).evaluation(evaluationParameter);
            if (ls instanceof Number) limitSize = ((Number) ls).longValue();
            Object lo = limit.getOffset();
            if (lo instanceof Expression) lo = ((Expression) lo).evaluation(evaluationParameter);
            if (lo instanceof Number) this.limitOffset = ((Number) lo).longValue();
        }
    }

    @Override
    public void visit(DMLSelectStatement node) {
        boolean verdictGroup = true;

        List<Expression> exprList = node.getSelectExprListWithoutAlias();
        if (verdictGroupFunc) {
            for (Expression expr : exprList) {
                if (!isGroupFuncPassthroughSelect(expr)) {
                    groupFuncType = GROUP_CANCEL;
                    verdictGroup = false;
                    break;
                }
            }
            limit(node.getLimit());
        }
        visitChild(2, false, verdictGroupFunc && verdictGroup, exprList);

        TableReference tr = node.getTables();
        visitChild(1, verdictColumn, verdictGroupFunc && verdictGroup, tr);

        Expression where = node.getWhere();
        visitChild(2, verdictColumn, false, where);

        GroupBy group = node.getGroup();
        visitChild(2, false, false, group);

        Expression having = node.getHaving();
        visitChild(2, verdictColumn, false, having);

        OrderBy order = node.getOrder();
        visitChild(2, false, false, order);
    }

    @Override
    public void visit(DMLSelectUnionStatement node) {
        visitChild(2, false, false, node.getOrderBy());
        visitChild(2, false, false, node.getSelectStmtList());
    }

    @Override
    public void visit(DMLUpdateStatement node) {
        TableReference tr = node.getTableRefs();
        visitChild(1, false, false, tr);

        List<Pair<Identifier, Expression>> assignmentList = node.getValues();
        if (assignmentList != null && !assignmentList.isEmpty()) {
            List<ASTNode> list = new ArrayList<ASTNode>(assignmentList.size() * 2);
            for (Pair<Identifier, Expression> p : assignmentList) {
                if (p == null) continue;
                list.add(p.getKey());
                list.add(p.getValue());
            }
            visitChild(2, false, false, list);
        }

        Expression where = node.getWhere();
        visitChild(2, verdictColumn, false, where);

        OrderBy order = node.getOrderBy();
        visitChild(2, false, false, order);
    }

    private void tableAsTableFactor(Identifier table) {
        int trim = table.trimParent(1, trimSchema);
        schemaTrimmed = schemaTrimmed || trim == Identifier.PARENT_TRIMED;
        customedSchema = customedSchema || trim == Identifier.PARENT_IGNORED;
        String tableName = table.getIdTextUpUnescape();
        tableAlias.put(null, tableName);
        tableAlias.put(tableName, tableName);
        addTable(tableName);
    }

    @Override
    public void visit(DMLDeleteStatement node) {
        TableReference tr = node.getTableRefs();
        List<Identifier> tbs = node.getTableNames();

        if (tr == null) {
            Identifier table = tbs.get(0);
            tableAsTableFactor(table);
        } else {
            visitChild(1, verdictColumn, false, tr);
            for (Identifier tb : tbs) {
                if (tb instanceof Wildcard) {
                    int trim = tb.trimParent(2, trimSchema);
                    schemaTrimmed = schemaTrimmed || trim == Identifier.PARENT_TRIMED;
                    customedSchema = customedSchema || trim == Identifier.PARENT_IGNORED;
                } else {
                    int trim = tb.trimParent(1, trimSchema);
                    schemaTrimmed = schemaTrimmed || trim == Identifier.PARENT_TRIMED;
                    customedSchema = customedSchema || trim == Identifier.PARENT_IGNORED;
                }
            }
        }

        Expression where = node.getWhereCondition();
        visitChild(2, verdictColumn, false, where);

        if (tr == null) {
            OrderBy order = node.getOrderBy();
            visitChild(2, false, false, order);
        }
    }

    private void insertReplace(DMLInsertReplaceStatement node) {
        Identifier table = node.getTable();
        List<Identifier> collist = node.getColumnNameList();
        QueryExpression query = node.getSelect();
        List<RowExpression> rows = node.getRowList();

        tableAsTableFactor(table);
        String tableName = table.getIdTextUpUnescape();

        visitChild(2, false, false, collist);

        if (query != null) {
            query.accept(this);
            return;
        }

        for (RowExpression row : rows) {
            visitChild(2, false, false, row);
        }

        Map<String, List<Object>> colVals = ensureColumnValueByTable(tableName);
        Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>> colValsIndex =
                ensureColumnValueIndexByTable(tableName);
        if (collist != null) {
            for (int i = 0; i < collist.size(); ++i) {
                String colName = collist.get(i).getIdTextUpUnescape();
                if (isRuledColumn(tableName, colName)) {
                    List<Object> valueList = ensureColumnValueList(colVals, colName);
                    Map<Object, Set<Pair<Expression, ASTNode>>> valMap =
                            ensureColumnValueIndexObjMap(colValsIndex, colName);
                    for (RowExpression row : rows) {
                        Expression expr = row.getRowExprList().get(i);
                        Object value = expr == null ? null : expr.evaluation(evaluationParameter);
                        if (value != Expression.UNEVALUATABLE) {
                            valueList.add(value);
                            addIntoColumnValueIndex(valMap, value, row, node);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void visit(DMLInsertStatement node) {
        insertReplace(node);

        List<Pair<Identifier, Expression>> dup = node.getDuplicateUpdate();
        if (dup != null) {
            ASTNode[] duplist = new ASTNode[dup.size() * 2];
            int i = 0;
            for (Pair<Identifier, Expression> p : dup) {
                Identifier key = null;
                Expression value = null;
                if (p != null) {
                    key = p.getKey();
                    value = p.getValue();
                }
                duplist[i++] = key;
                duplist[i++] = value;
            }
            visitChild(2, false, false, duplist);
        }
    }

    @Override
    public void visit(DMLReplaceStatement node) {
        insertReplace(node);
    }

    private void ddlTable(Identifier id, int idLevel) {
        visitChild(idLevel, false, false, id);
        addTable(id.getIdTextUpUnescape());
    }

    @Override
    public void visit(DDLTruncateStatement node) {
        ddlTable(node.getTable(), 1);
    }

    @Override
    public void visit(DDLAlterTableStatement node) {
        ddlTable(node.getTable(), 1);
    }

    @Override
    public void visit(DDLCreateIndexStatement node) {
        ddlTable(node.getTable(), 1);
        ddlTable(node.getIndexName(), 1);
    }

    @Override
    public void visit(DDLCreateTableStatement node) {
        ddlTable(node.getTable(), 1);
    }

    @Override
    public void visit(DDLRenameTableStatement node) {
        List<Pair<Identifier, Identifier>> list = node.getList();
        List<Identifier> idl = new ArrayList<Identifier>(list.size() * 2);
        for (Pair<Identifier, Identifier> p : list) {
            if (p != null) {
                if (p.getKey() != null) {
                    addTable(p.getKey().getIdTextUpUnescape());
                    idl.add(p.getKey());
                }
                idl.add(p.getValue());
            }
        }
        visitChild(1, false, false, idl);
    }

    @Override
    public void visit(DDLDropIndexStatement node) {
        ddlTable(node.getTable(), 1);
        ddlTable(node.getIndexName(), 1);
    }

    @Override
    public void visit(DDLDropTableStatement node) {
        visitChild(1, false, false, node.getTableNames());
        List<Identifier> tbs = node.getTableNames();
        if (tbs != null) {
            for (Identifier tb : tbs) {
                addTable(tb.getIdTextUpUnescape());
            }
        }
    }

    @Override
    public void visit(BetweenAndExpression node) {
        Expression fst = node.getFirst();
        Expression snd = node.getSecond();
        Expression trd = node.getThird();

        visitChild(2, false, false, fst, snd, trd);

        if (verdictColumn && !node.isNot() && fst instanceof Identifier) {
            Identifier col = (Identifier) fst;
            String table = tableAlias.get(col.getLevelUnescapeUpName(2));
            if (isRuledColumn(table, col.getIdTextUpUnescape())) {
                Object e1 = snd.evaluation(evaluationParameter);
                Object e2 = trd.evaluation(evaluationParameter);
                if (e1 != Expression.UNEVALUATABLE && e2 != Expression.UNEVALUATABLE && e1 != null && e2 != null) {
                    if (compareEvaluatedValue(e1, e2)) {
                        addColumnValue(table, col.getIdTextUpUnescape(), e1, node, null);
                    }
                }
            }
        }
    }

    /**
     * @param obj1 not null
     * @param obj2 not null
     */
    private static boolean compareEvaluatedValue(Object obj1, Object obj2) {
        if (obj1.equals(obj2)) return true;
        try {
            Pair<Number, Number> pair = ExprEvalUtils.convertNum2SameLevel(obj1, obj2);
            return pair.getKey().equals(pair.getValue());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void visit(ComparisionIsExpression node) {
        Expression operand = node.getOperand();
        visitChild(2, false, false, operand);

        if (verdictColumn && (operand instanceof Identifier)) {
            Identifier col = (Identifier) operand;
            String table = tableAlias.get(col.getLevelUnescapeUpName(2));
            if (isRuledColumn(table, col.getIdTextUpUnescape())) {
                switch (node.getMode()) {
                case ComparisionIsExpression.IS_FALSE:
                    addColumnValue(table, col.getIdTextUpUnescape(), LiteralBoolean.FALSE, node, null);
                    break;
                case ComparisionIsExpression.IS_TRUE:
                    addColumnValue(table, col.getIdTextUpUnescape(), LiteralBoolean.TRUE, node, null);
                    break;
                case ComparisionIsExpression.IS_NULL:
                    addColumnValue(table, col.getIdTextUpUnescape(), null, node, null);
                    break;
                }
            }
        }
    }

    @Override
    public void visit(InExpressionList node) {
        visitChild(2, false, false, node.getList());
    }

    @Override
    public void visit(BinaryOperatorExpression node) {
        Expression left = node.getLeftOprand();
        Expression right = node.getRightOprand();
        visitChild(2, false, false, left, right);
    }

    @Override
    public void visit(PolyadicOperatorExpression node) {
        // QS_TODO
    }

    @Override
    public void visit(ComparisionEqualsExpression node) {
        Expression left = node.getLeftOprand();
        Expression right = node.getRightOprand();
        visitChild(2, false, false, left, right);

        if (verdictColumn) {
            if (left instanceof Identifier) {
                comparisionEquals((Identifier) left, right.evaluation(evaluationParameter), false, node);
            } else if (right instanceof Identifier) {
                comparisionEquals((Identifier) right, left.evaluation(evaluationParameter), false, node);
            }
        }
    }

    @Override
    public void visit(ComparisionNullSafeEqualsExpression node) {
        Expression left = node.getLeftOprand();
        Expression right = node.getRightOprand();
        visitChild(2, false, false, left, right);

        if (verdictColumn) {
            if (left instanceof Identifier) {
                comparisionEquals((Identifier) left, right.evaluation(evaluationParameter), true, node);
            } else if (right instanceof Identifier) {
                comparisionEquals((Identifier) right, left.evaluation(evaluationParameter), true, node);
            }
        }
    }

    private void comparisionEquals(Identifier col, Object value, boolean nullsafe, Expression node) {
        if (value != Expression.UNEVALUATABLE && (nullsafe || value != null)) {
            String table = tableAlias.get(col.getLevelUnescapeUpName(2));
            if (isRuledColumn(table, col.getIdTextUpUnescape())) {
                addColumnValue(table, col.getIdTextUpUnescape(), value, node, null);
            }
        }
    }

    @Override
    public void visit(InExpression node) {
        Expression left = node.getLeftOprand();
        Expression right = node.getRightOprand();
        visitChild(2, false, false, left, right);

        if (verdictColumn && !node.isNot() && left instanceof Identifier && right instanceof InExpressionList) {
            Identifier col = (Identifier) left;
            String colName = col.getIdTextUpUnescape();
            String table = tableAlias.get(col.getLevelUnescapeUpName(2));

            if (isRuledColumn(table, colName)) {
                List<Object> valList = ensureColumnValueList(ensureColumnValueByTable(table), colName);
                Map<Object, Set<Pair<Expression, ASTNode>>> valMap =
                        ensureColumnValueIndexObjMap(ensureColumnValueIndexByTable(table), colName);
                InExpressionList inlist = (InExpressionList) right;
                for (Expression expr : inlist.getList()) {
                    Object value = expr.evaluation(evaluationParameter);
                    if (value != Expression.UNEVALUATABLE) {
                        valList.add(value);
                        addIntoColumnValueIndex(valMap, value, expr, node);
                    }
                }
            }
        }
    }

    @Override
    public void visit(LogicalAndExpression node) {
        for (int i = 0, len = node.getArity(); i < len; ++i) {
            Expression oprand = node.getOperand(i);
            visitChild(2, verdictColumn && isVerdictPassthroughWhere(oprand), false, oprand);
        }
    }

    @Override
    public void visit(LogicalOrExpression node) {
        for (int i = 0, len = node.getArity(); i < len; ++i) {
            Expression oprand = node.getOperand(i);
            visitChild(2, verdictColumn && isVerdictPassthroughWhere(oprand), false, oprand);
        }
    }

    @Override
    public void visit(Count node) {
        visitChild(2, false, false, node.getArguments());
        if (verdictGroupFunc) {
            if (groupFuncType != GROUP_NON && groupFuncType != GROUP_SUM || node.isDistinct()) {
                groupFuncType = GROUP_CANCEL;
            } else {
                groupFuncType = GROUP_SUM;
            }
        }
    }

    @Override
    public void visit(Sum node) {
        visitChild(2, false, false, node.getArguments());
        if (verdictGroupFunc) {
            if (groupFuncType != GROUP_NON && groupFuncType != GROUP_SUM || node.isDistinct()) {
                groupFuncType = GROUP_CANCEL;
            } else {
                groupFuncType = GROUP_SUM;
            }
        }
    }

    @Override
    public void visit(Max node) {
        visitChild(2, false, false, node.getArguments());
        if (verdictGroupFunc) {
            if (groupFuncType != GROUP_NON && groupFuncType != GROUP_MAX) {
                groupFuncType = GROUP_CANCEL;
            } else {
                groupFuncType = GROUP_MAX;
            }
        }
    }

    @Override
    public void visit(Min node) {
        visitChild(2, false, false, node.getArguments());
        if (verdictGroupFunc) {
            if (groupFuncType != GROUP_NON && groupFuncType != GROUP_MIN) {
                groupFuncType = GROUP_CANCEL;
            } else {
                groupFuncType = GROUP_MIN;
            }
        }
    }

    @Override
    public void visit(Identifier node) {
        int trim = node.trimParent(idLevel, trimSchema);
        schemaTrimmed = schemaTrimmed || trim == Identifier.PARENT_TRIMED;
        customedSchema = customedSchema || trim == Identifier.PARENT_IGNORED;
    }

    @Override
    public void visit(InnerJoin node) {
        TableReference tr1 = node.getLeftTableRef();
        TableReference tr2 = node.getRightTableRef();
        Expression on = node.getOnCond();
        visitChild(1, verdictColumn, verdictGroupFunc, tr1, tr2);
        visitChild(2, verdictColumn && isVerdictPassthroughWhere(on), false, on);
    }

    @Override
    public void visit(NaturalJoin node) {
        TableReference tr1 = node.getLeftTableRef();
        TableReference tr2 = node.getRightTableRef();
        visitChild(1, verdictColumn, verdictGroupFunc, tr1, tr2);
    }

    @Override
    public void visit(OuterJoin node) {
        TableReference tr1 = node.getLeftTableRef();
        TableReference tr2 = node.getRightTableRef();
        Expression on = node.getOnCond();
        visitChild(1, verdictColumn, verdictGroupFunc, tr1, tr2);
        visitChild(2, verdictColumn && isVerdictPassthroughWhere(on), false, on);
    }

    @Override
    public void visit(StraightJoin node) {
        TableReference tr1 = node.getLeftTableRef();
        TableReference tr2 = node.getRightTableRef();
        Expression on = node.getOnCond();
        visitChild(1, verdictColumn, verdictGroupFunc, tr1, tr2);
        visitChild(2, verdictColumn && isVerdictPassthroughWhere(on), false, on);
    }

    @Override
    public void visit(TableReferences node) {
        List<TableReference> list = node.getTableReferenceList();
        visitChild(1, verdictColumn, verdictGroupFunc, list);
    }

    @Override
    public void visit(SubqueryFactor node) {
        QueryExpression query = node.getSubquery();
        visitChild(2, verdictColumn, verdictGroupFunc, query);
    }

    @Override
    public void visit(TableRefFactor node) {
        Identifier table = node.getTable();
        visitChild(1, false, false, table);
        String tableName = table.getIdTextUpUnescape();
        addTable(tableName);
        String alias = node.getAliasUnescapeUppercase();
        if (alias == null) {
            tableAlias.put(null, tableName);
            tableAlias.put(tableName, tableName);
        } else {
            if (!tableAlias.containsKey(null)) {
                tableAlias.put(null, tableName);
            }
            tableAlias.put(alias, tableName);
        }
    }

    @Override
    public void visit(Dual dual) {
        this.dual = true;
    }

    // ------------------------------------------------------------------------------
    @Override
    public void visit(LikeExpression node) {
        visitChild(2, false, false, node.getFirst(), node.getSecond(), node.getThird());
    }

    @Override
    public void visit(CollateExpression node) {
        visitChild(2, false, false, node.getString());
    }

    @Override
    public void visit(UserExpression node) {
    }

    @Override
    public void visit(UnaryOperatorExpression node) {
        visitChild(2, false, false, node.getOperand());
    }

    @Override
    public void visit(FunctionExpression node) {
        visitChild(2, false, false, node.getArguments());
    }

    @Override
    public void visit(Char node) {
        visitChild(2, false, false, node.getArguments());
    }

    @Override
    public void visit(Convert node) {
        visitChild(2, false, false, node.getArguments());
    }

    @Override
    public void visit(Trim node) {
        visitChild(2, false, false, node.getArguments());
    }

    @Override
    public void visit(Cast node) {
        visitChild(2, false, false, node.getArguments());
        visitChild(2, false, false, node.getTypeInfo1(), node.getTypeInfo2());
    }

    @Override
    public void visit(Avg node) {
        visitChild(2, false, false, node.getArguments());
    }

    @Override
    public void visit(GroupConcat node) {
        visitChild(2, false, false, node.getArguments());
    }

    @Override
    public void visit(IntervalPrimary node) {
        visitChild(2, false, false, node.getQuantity());
    }

    @Override
    public void visit(Extract node) {
        visitChild(2, false, false, node.getArguments());
    }

    @Override
    public void visit(Timestampdiff node) {
        visitChild(2, false, false, node.getArguments());
    }

    @Override
    public void visit(Timestampadd node) {
        visitChild(2, false, false, node.getArguments());
    }

    @Override
    public void visit(GetFormat node) {
    }

    @Override
    public void visit(LiteralBitField node) {
    }

    @Override
    public void visit(LiteralBoolean node) {
    }

    @Override
    public void visit(LiteralHexadecimal node) {
    }

    @Override
    public void visit(LiteralNull node) {
    }

    @Override
    public void visit(LiteralNumber node) {
    }

    @Override
    public void visit(LiteralString node) {
    }

    @Override
    public void visit(CaseWhenOperatorExpression node) {
        visitChild(2, false, false, node.getComparee(), node.getElseResult());
        List<Pair<Expression, Expression>> whenPairList = node.getWhenList();
        if (whenPairList == null || whenPairList.isEmpty()) return;
        List<Expression> list = new ArrayList<Expression>(whenPairList.size() * 2);
        for (Pair<Expression, Expression> pair : whenPairList) {
            if (pair == null) continue;
            list.add(pair.getKey());
            list.add(pair.getValue());
        }
        visitChild(2, false, false, list);
    }

    @Override
    public void visit(DefaultValue node) {
    }

    @Override
    public void visit(ExistsPrimary node) {
        visitChild(2, false, false, node.getSubquery());
    }

    @Override
    public void visit(PlaceHolder node) {
    }

    @Override
    public void visit(MatchExpression node) {
        visitChild(2, false, false, node.getColumns());
        visitChild(2, false, false, node.getPattern());
    }

    @Override
    public void visit(ParamMarker node) {
    }

    @Override
    public void visit(RowExpression node) {
        visitChild(2, false, false, node.getRowExprList());
    }

    @Override
    public void visit(SysVarPrimary node) {
    }

    @Override
    public void visit(UsrDefVarPrimary node) {
    }

    @Override
    public void visit(IndexHint node) {
    }

    @Override
    public void visit(GroupBy node) {
        sortPairList(node.getOrderByList());
    }

    @Override
    public void visit(OrderBy node) {
        sortPairList(node.getOrderByList());
    }

    private void sortPairList(List<Pair<Expression, SortOrder>> list) {
        if (list == null || list.isEmpty()) return;
        Expression[] exprs = new Expression[list.size()];
        int i = 0;
        for (Pair<Expression, SortOrder> p : list) {
            exprs[i] = p.getKey();
            ++i;
        }
        visitChild(2, false, false, exprs);
    }

    @Override
    public void visit(Limit node) {
    }

    @Override
    public void visit(ColumnDefinition node) {
    }

    @Override
    public void visit(IndexOption node) {
    }

    @Override
    public void visit(IndexColumnName node) {
    }

    @Override
    public void visit(TableOptions node) {
    }

    @Override
    public void visit(AlterSpecification node) {
    }

    @Override
    public void visit(DataType node) {
    }

    @Override
    public void visit(ShowAuthors node) {
    }

    @Override
    public void visit(ShowBinaryLog node) {
    }

    @Override
    public void visit(ShowBinLogEvent node) {
    }

    @Override
    public void visit(ShowCharaterSet node) {
    }

    @Override
    public void visit(ShowCollation node) {
    }

    @Override
    public void visit(ShowColumns node) {
        tableMetaRead(node.getTable());
    }

    @Override
    public void visit(ShowContributors node) {
    }

    @Override
    public void visit(ShowCreate node) {
        if (node.getType() == ShowCreate.Type.TABLE) {
            tableMetaRead(node.getId());
        }
    }

    @Override
    public void visit(ShowDatabases node) {
    }

    @Override
    public void visit(ShowEngine node) {
    }

    @Override
    public void visit(ShowEngines node) {
    }

    @Override
    public void visit(ShowErrors node) {
    }

    @Override
    public void visit(ShowEvents node) {
        if (node.getSchema() != null) {
            schemaTrimmed = true;
            node.setSchema(null);
        }
        tableMetaRead(null);
    }

    @Override
    public void visit(ShowFunctionCode node) {
    }

    @Override
    public void visit(ShowFunctionStatus node) {
    }

    @Override
    public void visit(ShowGrants node) {
    }

    @Override
    public void visit(ShowIndex node) {
        tableMetaRead(node.getTable());
    }

    @Override
    public void visit(ShowMasterStatus node) {
    }

    @Override
    public void visit(ShowOpenTables node) {
        if (node.getSchema() != null) {
            schemaTrimmed = true;
            node.setSchema(null);
        }
        tableMetaRead(null);
    }

    @Override
    public void visit(ShowPlugins node) {
    }

    @Override
    public void visit(ShowPrivileges node) {
    }

    @Override
    public void visit(ShowProcedureCode node) {
    }

    @Override
    public void visit(ShowProcedureStatus node) {
    }

    @Override
    public void visit(ShowProcesslist node) {
    }

    @Override
    public void visit(ShowProfile node) {
    }

    @Override
    public void visit(ShowProfiles node) {
    }

    @Override
    public void visit(ShowSlaveHosts node) {
    }

    @Override
    public void visit(ShowSlaveStatus node) {
    }

    @Override
    public void visit(ShowStatus node) {
    }

    @Override
    public void visit(ShowTables node) {
        if (node.getSchema() != null) {
            schemaTrimmed = true;
            node.setSchema(null);
        }
        rewriteField = true;
        tableMetaRead(null);
    }

    @Override
    public void visit(ShowTableStatus node) {
        if (node.getDatabase() != null) {
            schemaTrimmed = true;
            node.setDatabase(null);
        }
        tableMetaRead(null);
    }

    @Override
    public void visit(ShowTriggers node) {
        if (node.getSchema() != null) {
            schemaTrimmed = true;
            node.setSchema(null);
        }
        tableMetaRead(null);
    }

    @Override
    public void visit(ShowVariables node) {
    }

    @Override
    public void visit(ShowWarnings node) {
    }

    @Override
    public void visit(DescTableStatement node) {
        tableMetaRead(node.getTable());
    }

    private void tableMetaRead(Identifier table) {
        if (table != null) {
            visitChild(1, false, false, table);
            addTable(table.getIdTextUpUnescape(), 0);
        }
        tableMetaRead = true;
    }

    @Override
    public void visit(DALSetStatement node) {
    }

    @Override
    public void visit(DALSetNamesStatement node) {
    }

    @Override
    public void visit(DALSetCharacterSetStatement node) {
    }

    @Override
    public void visit(DMLCallStatement node) {
    }

    @Override
    public void visit(MTSSetTransactionStatement node) {
    }

    @Override
    public void visit(MTSSavepointStatement node) {
    }

    @Override
    public void visit(MTSReleaseStatement node) {
    }

    @Override
    public void visit(MTSRollbackStatement node) {
    }

    @Override
    public void visit(ExtDDLCreatePolicy node) {
    }

    @Override
    public void visit(ExtDDLDropPolicy node) {
    }

}