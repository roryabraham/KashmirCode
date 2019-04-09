
//TODO: Fields not checked real-time, only on blur event

// First wait for the DOM to be fully loaded
$(function() {
    let $name = $("#name");
    let $email = $("#email");

    let nameValid = false;

    let validateName = function (event) {

        let nameRegex = /.+ .+/;

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
    };

    let validateEmail = function (event) {

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
    };

    // Register validation for events
    $name.keydown(validateName);
    $name.change(validateName);
    $email.keydown(validateEmail);
    $email.change(validateEmail);

    let validateForm = function (event) {
        if(!nameValid || !$email[0].checkValidity()) {
            event.preventDefault();
        }
    };

    // Register validation for entire form
    $("#submitButton").click(validateForm);
});