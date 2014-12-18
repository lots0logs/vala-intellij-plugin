package org.intellij.vala.reference;


import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.intellij.vala.ValaLanguageFileType;
import org.intellij.vala.psi.*;

import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class ResolveClassTest extends LightPlatformCodeInsightFixtureTestCase {

    public static final boolean NOT_STRICT = false;

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/org/intellij/vala/reference/test";
    }

    protected boolean isWriteActionRequired() {
        return false;
    }

    public void testDetectingElementAtCaret() {
        myFixture.configureByFiles("ResolveClassDefinitionInSameFile.vala");

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertThat(myFixture.getFile(), hasNoErrors());
        assertThat(elementAtCaret, hasParentOfType(ValaFieldDeclaration.class));
    }

    public void testResolveClassDefinitionInSameFile() {
        myFixture.configureByFiles("ResolveClassDefinitionInSameFile.vala");

        PsiElement referencedElement = getElementOfTypeAtCaret(ValaTypeWeak.class).getReference().resolve();

        assertThat(referencedElement, instanceOf(ValaClassDeclaration.class));
    }

    public void testResolveClassDefinitionInAnotherFile() {
        myFixture.configureByFiles("FileContainingClassReference.vala", "FileContainingClassDefinition.vala");

        PsiElement referencedElement = getElementOfTypeAtCaret(ValaTypeWeak.class).getReference().resolve();

        assertThat(referencedElement, allOf(instanceOf(ValaClassDeclaration.class), isInFile(hasName(containsString("FileContainingClassDefinition")))));
    }

    public void testResolveClassDefaultConstructorInAnotherFile() {
        myFixture.configureByFiles("FileContainingDefaultClassContructorReference.vala", "FileContainingClassDefinition.vala");

        PsiElement referencedElement = getElementOfTypeAtCaret(ValaObjectOrArrayCreationExpression.class).getReference().resolve();

        assertThat(referencedElement, allOf(instanceOf(ValaClassDeclaration.class), isInFile(hasName(containsString("FileContainingClassDefinition")))));
    }

    private PsiElement getElementOfTypeAtCaret(Class<? extends PsiElement> elementType) {
        return getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), elementType);
    }

    private static Matcher<PsiElement> hasParentOfType(final Class<? extends PsiElement> expectedPsiElement) {
        return new CustomTypeSafeMatcher<PsiElement>("has parent of type " + expectedPsiElement) {
            @Override
            protected boolean matchesSafely(PsiElement psiElement) {
                return getParentOfType(psiElement, expectedPsiElement, NOT_STRICT) != null;
            }
        };
    }

    private static Matcher<PsiElement> hasRootThat(final Matcher<? extends PsiElement> rootMatcher) {
        return new CustomTypeSafeMatcher<PsiElement>("has root that " + rootMatcher.toString()) {
            @Override
            protected boolean matchesSafely(PsiElement psiElement) {
                return rootMatcher.matches(getRoot(psiElement));
            }
        };
    }

    private static Matcher<PsiElement> hasNoErrors() {
        return new CustomTypeSafeMatcher<PsiElement>("has no errors") {

            @Override
            protected boolean matchesSafely(PsiElement element) {
                return !PsiTreeUtil.hasErrorElements(element);
            }
        };
    }

    private static PsiElement getRoot(PsiElement element) {
        while (element.getParent() != null) {
            element = element.getParent();
        }
        return element;
    }

    private static Matcher<PsiElement> isInFile(final Matcher<? extends PsiFile> psiFileMatcher) {
        return new CustomTypeSafeMatcher<PsiElement>("is contained in file that " + psiFileMatcher) {

            @Override
            protected boolean matchesSafely(PsiElement o) {
                PsiFile file = getParentOfType(o, PsiFile.class);
                return psiFileMatcher.matches(file);
            }
        };
    }

    private static Matcher<PsiFile> hasName(final Matcher<String> name) {
        return new CustomTypeSafeMatcher<PsiFile>("file with name " + name) {
            @Override
            protected boolean matchesSafely(PsiFile psiFile) {
                return name.matches(psiFile.getVirtualFile().getName());
            }
        };
    }
}
