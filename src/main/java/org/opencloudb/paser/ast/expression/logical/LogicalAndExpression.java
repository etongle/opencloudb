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
 * (created at 2011-1-12)
 */
package org.opencloudb.paser.ast.expression.logical;

import java.util.Map;

import org.opencloudb.paser.ast.expression.Expression;
import org.opencloudb.paser.ast.expression.PolyadicOperatorExpression;
import org.opencloudb.paser.ast.expression.function.literal.LiteralBoolean;
import org.opencloudb.paser.util.ExprEvalUtils;
import org.opencloudb.paser.visitor.SQLASTVisitor;

/**
 * @author mycat
 */
public class LogicalAndExpression extends PolyadicOperatorExpression {
    public LogicalAndExpression() {
        super(PRECEDENCE_LOGICAL_AND);
    }

    @Override
    public String getOperator() {
        return "AND";
    }

    @Override
    public Object evaluationInternal(Map<? extends Object, ? extends Object> parameters) {
        for (Expression operand : operands) {
            Object val = operand.evaluation(parameters);
            if (val == null) return null;
            if (val == UNEVALUATABLE) return UNEVALUATABLE;
            if (!ExprEvalUtils.obj2bool(val)) {
                return LiteralBoolean.FALSE;
            }
        }
        return LiteralBoolean.TRUE;
    }

    @Override
    public void accept(SQLASTVisitor visitor) {
        visitor.visit(this);
    }

}