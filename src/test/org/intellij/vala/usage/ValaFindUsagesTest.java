package org.intellij.vala.usage;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.intellij.vala.psi.ValaCreationMethodDeclaration;
import org.intellij.vala.psi.ValaSymbolPart;

import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.intellij.vala.psi.PsiMatchers.hasName;
import static org.intellij.vala.psi.PsiMatchers.hasParentOfType;
import static org.intellij.vala.psi.PsiMatchers.nameOfMethodDeclaration;
import static org.junit.Assert.assertThat;

public class ValaFindUsagesTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/org/intellij/vala/usage/test";
    }

    protected boolean isWriteActionRequired() {
        return false;
    }

    public void testSimpleMethodUsage() {
        Collection<UsageInfo> foundUsages = myFixture.testFindUsages("SimpleMethod.vala");

        assertThat(foundUsages, contains(resolvingTo(nameOfMethodDeclaration("some_method"))));
    }

    public void testTwoMethodUsages() {
        Collection<UsageInfo> foundUsages = myFixture.testFindUsages("TwoMethodUsages.vala");

        assertThat(foundUsages, contains(
                resolvingTo(nameOfMethodDeclaration("some_method")),
                resolvingTo(nameOfMethodDeclaration("some_method"))
        ));
    }

    public void testTwoConstructorUsages() {
        Collection<UsageInfo> foundUsages = myFixture.testFindUsages("TwoConstructorUsages.vala");

        assertThat(foundUsages, contains(
                resolvingTo(constructor("FooClass")),
                resolvingTo(constructor("FooClass"))));
    }

    public void testNamedConstructorUsage() {
        Collection<UsageInfo> foundUsages = myFixture.testFindUsages("NamedConstructorUsage.vala");

        assertThat(foundUsages, hasSize(1));
        assertThat(foundUsages, contains(resolvingTo(constructor("with_beer"))));
    }

    private static Matcher<PsiElement> constructor(String name) {
        return allOf(hasParentOfType(ValaCreationMethodDeclaration.class), hasName(name), instanceOf(ValaSymbolPart.class));
    }

    private static Matcher<UsageInfo> resolvingTo(final Matcher<? super PsiElement> resolutionTarget) {
        return new CustomTypeSafeMatcher<UsageInfo>("resolving to " + resolutionTarget) {
            @Override
            protected void describeMismatchSafely(UsageInfo item, Description mismatchDescription) {
                if (item.getReference() == null) {
                    mismatchDescription.appendText("reference was empty");
                } else {
                    resolutionTarget.describeMismatch(item.getReference().resolve(), mismatchDescription);
                }
            }

            @Override
            protected boolean matchesSafely(UsageInfo usageInfo) {
                final PsiReference reference = usageInfo.getReference();
                return reference != null && resolutionTarget.matches(reference.resolve());
            }
        };
    }
}
