
let $name = $("#name");
let $email = $("#email");

let nameValid = false;

let validateName = function (event) {

    // Validate name input
    if($name.length < 3 || !$name.value.match('/.+ .+/')) {
        nameValid = false;
        $name.addClass("invalid");
        event.preventDefault();
    }
    else {
        nameValid = true;
        $name.removeClass("invalid");
        $name.addClass("valid");
    }
};

let validateEmail = function (event) {

    // Email validation done by HTML5 (standard regex used, works for 99.99% of valid email addresses)
    if($email.checkValidity() === false) {
        $email.addClass("invalid");
        event.preventDefault();
    }
    else {
        $email.removeClass("invalid");
        $email.addClass("valid");
    }
};

// Register validation for any input change event
$name.change(validateName());
$email.change(validateEmail());

let validateForm = function (event) {
    if(!nameValid || !email.checkValidity())
    {
        event.preventDefault();
    }
    validateEmail(event);
};

// Register validation for entire form
$("#submitButton").click(validateForm());