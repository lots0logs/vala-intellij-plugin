package org.intellij.vala.psi.inference;


import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.intellij.vala.psi.*;

public class ExpressionTypeInference {

    public static ValaTypeDescriptor inferType(ValaPrimaryExpression primaryExpression) {
        if (primaryExpression.getLiteral() != null && hasNoChainedAccess(primaryExpression)) return primaryExpression.getLiteral().getTypeDescriptor();
        if (primaryExpression.getSimpleName() != null) {
            return inferType(primaryExpression.getSimpleName());
        }
        return null;
    }

    public static ValaTypeDescriptor inferType(ValaSimpleName valaSimpleName) {
        PsiReference reference = valaSimpleName.getReference();
        if (reference == null) {
            return null;
        }
        PsiElement resolved = reference.resolve();

        if (resolved instanceof HasTypeDescriptor) {
            return ((HasTypeDescriptor) resolved).getTypeDescriptor();
        }
        return null;
    }

    private static boolean hasNoChainedAccess(ValaPrimaryExpression primaryExpression) {
        return (primaryExpression.getChainAccessPartList().isEmpty());
    }
}
