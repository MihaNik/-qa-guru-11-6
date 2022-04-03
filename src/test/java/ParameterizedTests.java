import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class ParameterizedTests {

    @BeforeAll
    static void setup() {
        Configuration.browserSize = "1920x1080";
    }

    @BeforeEach
    void precondition() {
        open("http://www.baeldung.com/");
    }

    @AfterEach
    void closeBrowser() {
        closeWebDriver();
    }

    @ValueSource(strings = {"java operators", "jackson"})
    @ParameterizedTest(name = "Проверка  отображения статей по теме \"{0}\"")
    public void articleSearchCommonTest(String articleTheme) {
        $("ul#menu-main-menu li").click();
        $("input#search").setValue(articleTheme).pressEnter();
        $$("div#main article").findBy(text(articleTheme)).shouldBe(visible);
    }

    @CsvSource(value = {
            "operators| Walk through all Java operators to understand their functionalities and how to use them",
            "jackson| Learn how to serialize Boolean values into integers and numeric strings and how to deserialize them back"
    }, delimiter = '|')
    @ParameterizedTest(name = "Проверка отображения описания статей по теме \"{0}\"")
    public void articleSearchWithDescriptionCheckTest(String articleTheme, String articleDescription) {

        if ($(byText("AGREE")).is(visible)) { //cookies privacy popup
            $(byText("AGREE")).click();
        }

        $("ul#menu-main-menu li").click();
        $("input#search").setValue(articleTheme).pressEnter();
        $$("div#main article").findBy(text(articleTheme)).shouldBe(visible);
        $$("div#main article").findBy(text(articleTheme))
                .$("section.post-content")
                .shouldHave(exactText(articleDescription));

    }

    static Stream<Arguments> testDataProvider() {
        return Stream.of(
                Arguments.of("java", "Java Tutorials and Guides", 4, Arrays.asList("Java Tutorials", "Java Collections Tutorials")),
                Arguments.of("spring", "Spring Tutorials and Guides", 8, Arrays.asList("Spring REST Tutorials", "Spring Cloud Tutorials"))
        );
    }

    @MethodSource(value = "testDataProvider")
    @ParameterizedTest(name = "check {0} menu")
    void menuContentTest(String dataCategories, String dataTitle, int submenuCount, List<String> subTitles) {
        if ($(byText("AGREE")).is(visible)) { //cookies privacy popup
            $(byText("AGREE")).click();
        }

        SelenideElement data = $(" section[data-categories=" + dataCategories + "]");
        $("ul#menu-main-menu li").click();
        data.$("h4").shouldHave(exactText(dataTitle));

        data.$$("ul li").filterBy(visible).shouldHave(CollectionCondition.size(submenuCount));

        for (String st : subTitles
        ) {
            data.$$("ul li").findBy(exactText(st)).shouldBe(visible);
        }
    }
}
