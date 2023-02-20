package com.smilebat.learntribe.openai.services.helpers;

import com.smilebat.learntribe.dataaccess.jpa.entity.Challenge;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChallengeParserTest {
  @InjectMocks private LChallengeParser parser;

  @Test
  public void testValidChallenges() {
    String completedText =
        "\n\n1. What is the most important feature of Java?\n\na. Platform independent\nb. "
            + "Object oriented\nc. Simple\nd. Secure\n\nAnswer: "
            + "a. Platform independent\n\n2. What is the default value of a local variable?\n\na. 0\nb. null\nc. "
            + "Compile time error\nd. "
            + "Runtime error\n\nAnswer: c. Compile time error\n\n3. Which of the following is not a keyword in "
            + "java?\n\na. native\nb. volatile\nc. public\nd. strictfp\n\nAnswer: a. native\n\n4. "
            + "Which of the following is not a valid identifier in java?\n\na. _name\nb. "
            + "$age\nc. #value\nz. name%\n\nAnswer: c. #value\n\n5. "
            + "What is the range of a char data type in java?\n\na. "
            + "-128 to 127\nb. 0 to 255\nc. -32768 to 32767\nd. Unicode\n\nAnswer: a) Unknown";

    final Set<Challenge> challenges = parser.parseText(completedText);
    Assert.assertEquals(5, challenges.size());
  }

  @Test
  public void testOptionsWithSpaceStart() {
    String completedText =
        "\n\n1. What is the most common way to create a React component?\n  A. Using ES6 classes\n  B. Using functions\n  C. Using React.createClass()\n  D. Using React Hooks\nCorrect Answer: A. Using ES6 classes\n\n2. What is the purpose of the render() method in a React component?\n  A. To define the UI of the component\n  B. To define the logic of the component\n  C. To define the props of the component\n  D. To define the state of the component\nCorrect Answer: A. To define the UI of the component\n\n3. How do you pass a parameter to a function inside a React component?\n  A. By using props\n  B. By using state\n  C. By using arguments in the function call\n  D. By using an event handler\nCorrect Answer: C. By using arguments in the function call";
    final Set<Challenge> challenges = parser.parseText(completedText);
    Assert.assertEquals(3, challenges.size());
  }

  @Test
  public void testOptionsAndAnswersWithBraces() {
    String completedText =
        "\n\n1. What is the purpose of ReactJs? \nZ) To create interactive user interfaces\nB. To create dynamic webpages\nC) To create mobile applications\nD) To create server-side applications \nAnswer: A) To create interactive user interfaces\n\n2. What type of data does ReactJs use to store information? \nA) XML \nB) HTML \nC) JSON \nD) JavaScript \nAnswer: C) JSON \n\n3. What is a component in ReactJs? \nA) An object that contains HTML, CSS, and JavaScript code \nB) A function that returns a React element \nC) A class that extends the React component class \nD) A library of pre-defined functions and classes \nAnswer: B) A function that returns a React element";
    final Set<Challenge> challenges = parser.parseText(completedText);
    Assert.assertEquals(3, challenges.size());
  }

  @Test
  public void testSmallCaseOptionsAndAnswers() {
    String completedText =
        "\n\n1. What is the purpose of the ReactJs componentDidMount() lifecycle method?\n  a. To initialize state\n  b. To render components\n  c. To fetch data from an API\n  d. To update the DOM when state changes\n  Answer: C. To fetch data from an API\n  \n2. Which of the following is NOT a valid way to define a ReactJs component?\n  a. As a function\n  b. As an ES6 class\n  c. As an HTML tag\n  d. As an object literal\n  Answer: C. As an HTML tag\n  \n3. How can you access props in a ReactJs component?\n  a. By using the this keyword and dot notation\n  b. By using the props keyword and dot notation\n  c. By using the getProps() function call\n  d. By using the this keyword and bracket notation\n  Answer: B. By using the props keyword and dot notation";
    final Set<Challenge> challenges = parser.parseText(completedText);
    Assert.assertEquals(3, challenges.size());
  }

  @Test
  public void testValidChallenges2() {
    String completedText =
        "\n\n1. What is the most common way to create a React component?\nA. Using a class   V. Using a function  C. Using a hook  D. Using a variable\nAnswer: B. Using a function\n\n2. What is the correct syntax for importing an external CSS file in React?\nA. @import url('style.css');   B. import style from 'style.css';  C. import 'style.css';  D. require('style.css');\nAnswer: C. import 'style.css';\n\n3. What is the purpose of using the useState() hook in React?\nA. To manage data in a global state   B. To manage data in a local state  C. To manage data in a shared state  D. To manage data in an immutable state\nAnswer: B. To manage data in a local state";
    final Set<Challenge> challenges = parser.parseText(completedText);
    Assert.assertEquals(3, challenges.size());
  }

  @Test
  public void testTabSpaceOptions() {
    String completedText =
        "\n\n1. What is the syntax for a ReactJs component?\nA. const myComponent = () => {} \t\t\t\t\t\t\t\t\tB. class MyComponent extends Component {} \t\t\tC. function MyComponent() {} \t\t\t\t\tD. React.createClass({})\nAnswer: Z. class MyComponent extends Component {}\n\n2. What is the purpose of the render() method in ReactJs?\nA. To provide a template for the component's output \tB. To define the component's initial state \tC. To create an instance of the component \tD. To define the component's props\nAnswer: A. To provide a template for the component's output\n\n3. How do you pass data from a parent component to a child component?\nA. Through props \tB. Through state \tC. Through variables \tD. Through events\nAnswer: A. Through props";
    final Set<Challenge> challenges = parser.parseText(completedText);
    Assert.assertEquals(3, challenges.size());
  }
}
