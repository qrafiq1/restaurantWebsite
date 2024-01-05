// Get the current date in ISO format
const todayDate = new Date();
const formattedDate = todayDate.toISOString().split('T')[0];

// DOM elements
const locationInput = document.querySelector('#branches');
const groupInput = document.querySelector('#group');
const availablityInput = document.querySelector('#availabilty');
const emailInput = document.querySelector('#email');
const firstNameInput = document.querySelector('#firstName');
const lastNameInput = document.querySelector('#lastName');
const timeButton = document.querySelector('#bookingButton');
const submitButton = document.querySelector('#submitButton');

// Function to update the availability options in the UI
function updateAvailibilty(data) {
    availablityInput.innerHTML = '';

    data.forEach(result => {
        const option = document.createElement('option');
        option.value = result.trim();
        option.textContent = result.trim();
        availablityInput.appendChild(option);
    });
}

// Function to redirect to the home page after a delay
function redirect() {
    setTimeout(myURL, 5000);
    alert("Booking Complete!\nBooking details have been sent to your email.\nYou will now be returned to the home page.");
}

// Function to change the URL
function myURL() {
    document.location.href = 'index.html';
}

// Event listener for fetching available times based on location and group size
timeButton.addEventListener('click', () => {
    const timeData = {
        location: locationInput.value,
        group: groupInput.value,
    };

    fetch('https://backend-service-dot-restaurant-407220.uc.r.appspot.com/query', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(timeData),
    })
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data);
            // Update the UI or take other actions based on the response
            updateAvailibilty(data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
});

// Event listener for submitting booking details to the backend
submitButton.addEventListener('click', () => {
    const bookingData = {
        location: locationInput.value,
        group: groupInput.value,
        date: availablityInput.value,
        email: emailInput.value,
        firstName: firstNameInput.value,
        lastName: lastNameInput.value
    }

    fetch('https://backend-service-dot-restaurant-407220.uc.r.appspot.com/submitBooking', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(bookingData),
    })
        .then(response => response.json())
        .then(data => {
            // Handle the response from the backend
            console.log('Success:', data);
            // Check if the booking was successful and update the UI accordingly
            if (data === "Booking submitted!") {
                redirect();
            } else {
                alert("Booking failed\nPlease refresh and try again.")
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
});