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
 * (created at 2012-8-13)
 */
package org.opencloudb.paser.ast.ddl;

import org.opencloudb.paser.ast.ASTNode;
import org.opencloudb.paser.ast.ddl.datatype.DataType;
import org.opencloudb.paser.ast.expression.Expression;
import org.opencloudb.paser.ast.expression.function.literal.LiteralString;
import org.opencloudb.paser.visitor.SQLASTVisitor;

/**
 * NOT FULL AST
 * 
 * @author mycat
 */
public class ColumnDefinition implements ASTNode {
    public static enum SpecialIndex {
        PRIMARY,
        UNIQUE,
    }

    public static enum ColumnFormat {
        FIXED,
        DYNAMIC,
        DEFAULT,
    }

    private final DataType dataType;
    private final boolean notNull;
    private final Expression defaultVal;
    private final boolean autoIncrement;
    private final SpecialIndex specialIndex;
    private final LiteralString comment;
    private final ColumnFormat columnFormat;

    /**
     * @param dataType
     * @param notNull
     * @param defaultVal might be null
     * @param autoIncrement
     * @param specialIndex might be null
     * @param comment might be null
     * @param columnFormat might be null
     */
    public ColumnDefinition(DataType dataType, boolean notNull, Expression defaultVal, boolean autoIncrement,
                            SpecialIndex specialIndex, LiteralString comment, ColumnFormat columnFormat) {
        if (dataType == null) throw new IllegalArgumentException("data type is null");
        this.dataType = dataType;
        this.notNull = notNull;
        this.defaultVal = defaultVal;
        this.autoIncrement = autoIncrement;
        this.specialIndex = specialIndex;
        this.comment = comment;
        this.columnFormat = columnFormat;
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public Expression getDefaultVal() {
        return defaultVal;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public SpecialIndex getSpecialIndex() {
        return specialIndex;
    }

    public LiteralString getComment() {
        return comment;
    }

    public ColumnFormat getColumnFormat() {
        return columnFormat;
    }

    @Override
    public void accept(SQLASTVisitor visitor) {
        visitor.visit(this);
    }

}