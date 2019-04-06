
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
            event.preventDefault();
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
            event.preventDefault();
        }
        else {
            if($email.hasClass("invalid")) {
                $email.removeClass("invalid");
            }
            $email.addClass("valid");
        }
    };

    // Register validation for any input change event
    $name.change(validateName);
    $email.change(validateEmail);

    let validateForm = function (event) {
        if(!nameValid || !$email[0].checkValidity())
        {
            event.preventDefault();
        }
        validateEmail(event);
    };

    // Register validation for entire form
    $("#submitButton").click(validateForm);
});