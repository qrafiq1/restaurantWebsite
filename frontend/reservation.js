const todayDate = new Date();
const formattedDate = todayDate.toISOString().split('T')[0];

const locationInput = document.querySelector('#branches');
const groupInput = document.querySelector('#group');
const availablityInput = document.querySelector('#availabilty');
const emailInput = document.querySelector('#email');
const firstNameInput = document.querySelector('#firstName');
const lastNameInput = document.querySelector('#lastName');
const timeButton = document.querySelector('#bookingButton');
const submitButton = document.querySelector('#submitButton');

function updateAvailibilty(data) {
    availablityInput.innerHTML = '';

    data.forEach(result => {
        const option = document.createElement('option');
        option.value = result.trim();
        option.textContent = result.trim();
        availablityInput.appendChild(option);
    });
}


//sends info to backend and returns updated list of available times to pick
timeButton.addEventListener('click', () => {
    const timeData = {
        location: locationInput.value,
        group: groupInput.value,
    };

    fetch('http://localhost:8080/query', {
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

//sends booking info with user info to backend to complete booking
//if complete == true, send to webpage saying complete
//would also intiate email sending here
submitButton.addEventListener('click', () => {
    const bookingData = {
        location: locationInput.value,
        group: groupInput.value,
        date: availablityInput.value,
        email: emailInput.value,
        firstName: firstNameInput.value,
        lastName: lastNameInput.value
    }

    fetch('http://localhost:8080/submitBooking', {
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
            if (data.complete) {
                // Booking is complete, show success message or navigate to a success page
            } else {
                // Booking failed, handle the error
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
});

