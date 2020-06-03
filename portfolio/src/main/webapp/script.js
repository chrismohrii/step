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
 * Adds a new number for a given button in the game. 
 */
function addRandomNumber(container){
    const numbers = 
        ['1', '2', '3', "4", "5"];
    
  // Pick a number.
  const number = numbers[Math.floor(Math.random() * numbers.length)];


  // Add it to the page.
  const numberContainer = document.getElementById(container);
  numberContainer.innerText = number;
  hasWon();
    
}

/*
* Checks if the game is won. 
*/
function hasWon(){
  const winBox = document.getElementById('win-container');
  var firstNumber = document.getElementById('number1-container').innerText;
  var secondNumber = document.getElementById('number2-container').innerText;
  var thirdNumber = document.getElementById('number3-container').innerText;

  // Compare the numbers for appropriate message. 
  if (firstNumber === secondNumber && firstNumber == thirdNumber){
    winBox.innerText = "You Win!";
    }
  else {
    winBox.innerText = "";
    }
}

/**
 * Fetches the current state of the game and builds the UI.
 */
async function getCommentSection() {
  fetch('/data?maxComments=5').then(response => response.json()).then((comments) => {

    // Build the list of history entries.
    const historyEl = document.getElementById('history');
    comments.forEach((comment) => {
    historyEl.appendChild(createListElement(comment));
    });
  });
}

/** Creates an <p> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('p');
  liElement.innerText = text;
  return liElement;
}
