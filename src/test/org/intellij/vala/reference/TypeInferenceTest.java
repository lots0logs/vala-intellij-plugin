package org.intellij.vala.reference;

import com.intellij.psi.PsiElement;
import org.intellij.vala.psi.BasicTypeDescriptor;
import org.intellij.vala.psi.ValaExpression;
import org.intellij.vala.psi.ValaIdentifier;
import org.intellij.vala.psi.ValaTypeDescriptor;
import org.intellij.vala.psi.inference.ExpressionTypeInference;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.intellij.vala.psi.PsiMatchers.nameOfMethodDeclaration;
import static org.intellij.vala.psi.impl.QualifiedNameBuilder.nameOf;
import static org.junit.Assert.assertThat;

public class TypeInferenceTest extends ValaReferenceTestBase {

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/org/intellij/vala/inference/test";
    }

    public void testInferFromVariableReference() {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType(), is((ValaTypeDescriptor) BasicTypeDescriptor.INTEGER));
    }

    public void testInferTypeFromMethodCall() throws IOException {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType(), is((ValaTypeDescriptor) BasicTypeDescriptor.LONG));
    }

    public void testInferFromLiteral() {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType(), is((ValaTypeDescriptor) BasicTypeDescriptor.STRING));
    }

    public void testInferTypeFromObjectCreation() throws IOException {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType().getQualifiedName(), is(equalTo(nameOf("MyClass"))));
    }

    public void testInferTypeFromObjectCreationForClassWithExplicitConstructor() throws IOException {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType().getQualifiedName(), is(equalTo(nameOf("MyClass"))));
    }

    public void testInferFromOtherClassFieldReference() throws IOException {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType(), is((ValaTypeDescriptor) BasicTypeDescriptor.INTEGER));
    }

    public void testInferFromVariableReferenceChain() throws IOException {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType(), is((ValaTypeDescriptor) BasicTypeDescriptor.INTEGER));
    }

    public void testInferFromSizeof() throws IOException {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType(), is((ValaTypeDescriptor) BasicTypeDescriptor.LONG));
    }

    public void testInferFromMultiplication() throws IOException {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType(), is((ValaTypeDescriptor) BasicTypeDescriptor.DOUBLE));
    }

    public void testInferFromConditionalExpression() throws IOException {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType(), is((ValaTypeDescriptor) BasicTypeDescriptor.DOUBLE));
    }

    public void testInferFromCoalescingExpression() throws IOException {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType().getQualifiedName(), is(equalTo(nameOf("MyClass"))));
    }

    public void testMethodReferenceOnInferredObjectType() throws IOException {
        myFixture.configureByFiles(getTestName(false) + ".vala");

        PsiElement referenced = getElementOfTypeAtCaret(ValaIdentifier.class).getReference().resolve();
        assertThat(referenced, nameOfMethodDeclaration("getName"));
    }

    public void testInferFromCast() throws IOException {
        myFixture.configureByFiles(getTestName(false) + ".vala");
        assertThat(inferredType().getQualifiedName(), is(equalTo(nameOf("B"))));
    }

    private ValaTypeDescriptor inferredType() {
        ValaExpression expression = getElementOfTypeAtCaret(ValaExpression.class);
        return ExpressionTypeInference.inferType(expression);
    }
}
