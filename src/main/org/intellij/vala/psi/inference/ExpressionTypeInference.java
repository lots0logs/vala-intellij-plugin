package org.intellij.vala.psi.inference;


import com.google.common.collect.ImmutableList;
import org.apache.http.util.Asserts;
import org.intellij.vala.psi.*;
import org.intellij.vala.psi.impl.ValaPsiElementUtil;

import java.util.List;

import static org.intellij.vala.psi.impl.ValaPsiImplUtil.getTypeDescriptor;

public class ExpressionTypeInference {

    public static ValaTypeDescriptor inferType(ValaExpression valaExpression) {
        if (valaExpression instanceof ValaPrimaryExpression) {
            return inferType((ValaPrimaryExpression) valaExpression);
        } else if (valaExpression instanceof ValaObjectOrArrayCreationExpression) {
            return getTypeDescriptor((ValaObjectOrArrayCreationExpression) valaExpression);
        } else if(valaExpression instanceof ValaSizeofExpression) {
            return inferType((ValaSizeofExpression) valaExpression);
        } else if(valaExpression instanceof ValaMultiplicativeExpression) {
            return inferType((ValaMultiplicativeExpression) valaExpression);
        } else if(valaExpression instanceof ValaAdditiveExpression) {
            return inferType((ValaAdditiveExpression) valaExpression);
        } else if (valaExpression instanceof ValaUnaryExpression) {
            return inferType((ValaUnaryExpression) valaExpression);
        } else if (valaExpression instanceof ValaConditionalExpression) {
            return inferType((ValaConditionalExpression) valaExpression);
        } else if (isBooleanTypeExpression(valaExpression)) {
            return BasicTypeDescriptor.BOOL;
        } else if (valaExpression instanceof ValaShiftExpression) {
            return inferType((ValaShiftExpression) valaExpression);
        } else if (valaExpression instanceof ValaTypeofExpression) {
            return inferType((ValaTypeofExpression) valaExpression);
        } else if (valaExpression instanceof ValaCoalescingExpression) {
            return inferType((ValaCoalescingExpression) valaExpression);
        } else if (valaExpression instanceof ValaTypeCastExpression) {
            return inferType((ValaTypeCastExpression) valaExpression);
        } else if (valaExpression instanceof ValaRelationalExpression) {
            return inferType((ValaRelationalExpression) valaExpression);
        } else if (valaExpression instanceof ValaSimpleExpression) {
            return inferType((ValaSimpleExpression) valaExpression);
        }
        return null;
    }

    private static ValaTypeDescriptor inferType(ValaTypeCastExpression valaExpression) {
        return valaExpression.getType().getTypeDescriptor();
    }

    private static boolean isBooleanTypeExpression(ValaExpression valaExpression) {
        return valaExpression instanceof ValaConditionalAndExpression
                || valaExpression instanceof ValaConditionalOrExpression
                || valaExpression instanceof ValaEqualityExpression;
    }

    public static ValaTypeDescriptor inferType(ValaPrimaryExpression primaryExpression) {
        if (hasNoChainedAccess(primaryExpression)) {
            return inferType((ValaSimpleExpression) primaryExpression.getExpression());
        } else {
            return ValaPsiElementUtil.getLastPart(primaryExpression).getTypeDescriptor();
        }
    }

    public static ValaTypeDescriptor inferType(ValaSimpleExpression simpleExpression) {
        if (simpleExpression instanceof ValaSimpleName) {
            return inferType((ValaSimpleName) simpleExpression);
        } else if (simpleExpression instanceof ValaTypeofExpression) {
            return inferType((ValaTypeofExpression) simpleExpression);
        } else if (simpleExpression instanceof ValaSizeofExpression) {
            return inferType((ValaSizeofExpression) simpleExpression);
        } else if (simpleExpression instanceof ValaLiteral) {
            return ((ValaLiteral) simpleExpression).getTypeDescriptor();
        } else {
            return null;
        }
    }

    private static ValaTypeDescriptor inferType(ValaShiftExpression shiftExpression) {
        return inferType(shiftExpression.getExpression());
    }

    private static ValaTypeDescriptor inferTypeForNumericTypes(ValaTypeDescriptor left, ValaTypeDescriptor right) {
        if (left == BasicTypeDescriptor.DOUBLE || right == BasicTypeDescriptor.DOUBLE) return BasicTypeDescriptor.DOUBLE;
        if (left == BasicTypeDescriptor.LONG || right == BasicTypeDescriptor.LONG) return BasicTypeDescriptor.LONG;
        return left;
    }

    public static ValaTypeDescriptor inferType(ValaMultiplicativeExpression multiplicativeExpression) {
        return inferTypeFromBinaryNumericExpression(multiplicativeExpression.getExpression(), multiplicativeExpression.getExpressionList());
    }

    public static ValaTypeDescriptor inferType(ValaCoalescingExpression coalescingExpression) {
        return inferType(coalescingExpression.getExpression());
    }

    public static ValaTypeDescriptor inferType(ValaAdditiveExpression additiveExpression) {
        return inferTypeFromBinaryNumericExpression(additiveExpression.getExpression(), additiveExpression.getExpressionList());
    }

    public static ValaTypeDescriptor inferType(ValaConditionalExpression conditionalExpression) {
        final List<ValaExpression> expressionList = conditionalExpression.getExpressionList();
        Asserts.check(expressionList.size() == 3, "Invalid conditional expression");
        return inferTypeFromBinaryNumericExpression(expressionList.get(1), ImmutableList.of(expressionList.get(2)));
    }

    private static ValaTypeDescriptor inferTypeFromBinaryNumericExpression(ValaExpression first, List<ValaExpression> remainingExpressions) {
        if (first == null) {
            return null;
        }
        ValaTypeDescriptor firstType = inferType(first);
        if (firstType == null) {
            return null;
        }
        for (ValaExpression right : remainingExpressions) {
            ValaTypeDescriptor rightType = inferType(right);
            if (rightType == null) {
                return null;
            }
            firstType = inferTypeForNumericTypes(firstType, rightType);
        }
        return firstType;
    }

    public static ValaTypeDescriptor inferType(ValaUnaryExpression unaryExpression) {
        if (unaryExpression.getType() != null) {
            return unaryExpression.getType().getTypeDescriptor();
        }
        return ExpressionTypeInference.inferType(unaryExpression.getExpression());
    }

    public static ValaTypeDescriptor inferType(ValaSizeofExpression sizeofExpression) {
        return BasicTypeDescriptor.LONG;
    }

    public static ValaTypeDescriptor inferType(ValaTypeofExpression typeofExpression) {
        return new ValaTypeTypeDescriptor();
    }

    public static ValaTypeDescriptor inferType(ValaSimpleName valaSimpleName) {
        return valaSimpleName.resolve().map(resolved -> {
            if (resolved instanceof HasTypeDescriptor) {
                return ((HasTypeDescriptor) resolved).getTypeDescriptor();
            }
            return null;
        }).orElse(null);
    }

    public static ValaTypeDescriptor inferType(ValaRelationalExpression valaRelationalExpression) {
        List<ValaExpression> expressions = valaRelationalExpression.getExpressionList();
        ValaTypeDescriptor result = null;
        if (expressions.size() == 1) {
            result = inferType(expressions.get(0));
        } else if (expressions.size() == 2) {
            final ValaExpression lastExpression = expressions.get(1);
            if (lastExpression instanceof ValaTypeCastExpression) {
                result = inferType((ValaTypeCastExpression) lastExpression);
            } else if (lastExpression instanceof ValaTypeCheckExpression) {
                result = BasicTypeDescriptor.BOOL;
            }
        }
        return result;
    }

    private static boolean hasNoChainedAccess(ValaPrimaryExpression primaryExpression) {
        return (primaryExpression.getChainAccessPartList().isEmpty());
    }
}
