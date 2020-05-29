// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Adds a random fun fact to the page.
 */
function addRandomFunFact(){
    const facts = 
        ['Chris has a twin sister', 'Chris is trilingual', 'Chris knows Python'];
    
  // Pick a fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];


  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
    

}

/**
 * Adds a new number for first button in the game. 
 */
function addRandomNumber1(){
    const numbers = 
        ['1', '2', '3', "4", "5"];
    
  // Pick a number.
  const number = numbers[Math.floor(Math.random() * numbers.length)];


  // Add it to the page.
  const numberContainer = document.getElementById('number1-container');
  numberContainer.innerText = number;
  hasWon();
    

}

/**
 * Adds a new number for second button in the game. 
 */
function addRandomNumber2(){
    const numbers = 
        ['1', '2', '3', "4", "5"];
    
  // Pick a number.
  const number = numbers[Math.floor(Math.random() * numbers.length)];


  // Add it to the page.
  const numberContainer = document.getElementById('number2-container');
  numberContainer.innerText = number;
  hasWon();

}

/**
 * Adds a new number for third button in the game. 
 */
function addRandomNumber3(){
    const numbers = 
        ['1', '2', '3', "4", "5"];
    
  // Pick a number.
  const number = numbers[Math.floor(Math.random() * numbers.length)];


  // Add it to the page.
  const numberContainer = document.getElementById('number3-container');
  numberContainer.innerText = number;   
  hasWon(); 

}

/*
* Checks if the game is won. 
*/
function hasWon(){
      const first = document.getElementById('number1-container');
      const second = document.getElementById('number2-container');
      const third = document.getElementById('number3-container');
      const winBox = document.getElementById('win-container');
      var firstNumber = first.innerText;
      var secondNumber = second.innerText;
      var thirdNumber = third.innerText;

      // Compare the numbers for appropriate message. 
      if (firstNumber === secondNumber && firstNumber == thirdNumber){
        winBox.innerText = "You Win!";
      }
      else {
          winBox.innerText = "";
      }
}
