/*
    File: validate.js
    Author: Rory Abraham
    Since: 4/15/19
    Dependent on: jQuery
 */

// First wait for the DOM to be fully loaded
$(function() {
    const $name = $("#name");
    const $email = $("#email");
    const $form = $("form")[0];
    const $formElements = $("form input, form textarea");

    let nameValid = false;

    const resetForm = function (event) {
        alert("Form timed out!");
        $form.reset();
    };

    // Timeout form after 75 seconds of inactivity
    let timeout = setTimeout(resetForm, 75000);

    const resetTimeout = function(event) {
        clearTimeout(timeout);
        timeout = setTimeout(resetForm, 75000);
    };

    // validation function for name input element
    const validateName = function (event) {

        const nameRegex = /.+ .+/;

        // Validate name input
        if($name[0].length < 3 || !nameRegex.test($name[0].value)) {
            nameValid = false;
            $name.addClass("invalid");
        }
        else {
            nameValid = true;
            if($name.hasClass("invalid")) {
                $name.removeClass("invalid");
            }
            $name.addClass("valid");
        }

        resetTimeout();
    };

    // validation function for email input element
    const validateEmail = function (event) {

        // Email validation done by HTML5 (standard regex used, works for 99.99% of valid email addresses)
        if($email[0].checkValidity() === false) {
            $email.addClass("invalid");
        }
        else {
            if($email.hasClass("invalid")) {
                $email.removeClass("invalid");
            }
            $email.addClass("valid");
        }

        resetTimeout();
    };

    // Register validation for events
    $name.keypress(validateName);
    $name.change(validateName);
    $email.keypress(validateEmail);
    $email.change(validateEmail);

    // Register timeout reset for all other form input elements
    $formElements.change(resetTimeout);
    $formElements.keypress(resetTimeout);

    // Validation function for entire form
    const validateForm = function (event) {
        if(!nameValid || !$email[0].checkValidity()) {
            event.preventDefault();
        }
        else {
            clearTimeout(timeout);

        }
    };

    // Register validation for entire form
    $("#submitButton").click(validateForm);
});