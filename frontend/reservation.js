const todayDate = new Date();
const formattedDate = todayDate.toISOString().split('T')[0];

const location = document.querySelector('#branches');
const group = document.querySelector('#group');
const dateControl = document.querySelector('#date');
const timeRange = document.querySelector('#time');
const email = document.querySelector('#email');
const firstName = document.querySelector('#firstName');
const lastName = document.querySelector('#lastName');
const timeButton = document.querySelector('#timeButton');
const submitButton = document.querySelector('#submitButton');

dateControl.value = formattedDate;

timeButton.addEventListener('click', () => {
    //sends info to backend and returns updated list of available times to pick
});

submitButton.addEventListener('click', () => {
    //sends booking info with user info to backend to complete booking
    //if complete == true, send to webpage saying complete
    //would also intiate email sending here
});

