let currentSlideIndex = 0;
let displayState = false;
const fadeInDuration = 6000;
const fadeOutDuration = 3000;

randomIntInRange = (min, max) => {
    return Math.floor(Math.random() * (max - min + 1)) + min;
};

/*displayRandomSlide = (slides) => {
    let slideIndex = randomIntInRange(1, slides.length);
    while(slideIndex === currentSlideIndex) {
        slideIndex = randomIntInRange(1, slides.length);
    }
    slides.eq(slideIndex - 1).fadeIn(fadeInDuration);
    currentSlideIndex = slideIndex;
};

slideTransition = () => {
    let slides = $(".slide");

    // If there is a currently displayed slide
    if(displayState) {
        // Fade current slide most of the way out
        for(let i=0; i < slides.length; i++) {
            let currSlide = slides.eq(i);
            if(currSlide.css("display") !== "none") {
                currSlide.fadeTo(fadeOutDuration, 0.3, () => {
                    // Then fade in the new slide
                    displayRandomSlide(slides);
                    // And fade out the current slide the rest of the way (simultaneously)
                    currSlide.fadeOut("fast");
                });
            }
        }
    }
    else {
        // Just display the new slide
        displayRandomSlide(slides);
        displayState = true;
    }

    // repeat every 6.5 seconds
    setTimeout(slideTransition, 14000);
};*/

/*
const slideTransition = () => {

    let slides = $(".slides");

    // find the image on the top of the pile
    let active = $("#hero_image .active");

    // choose the next image
    let slideIndex = randomIntInRange(1, slides.length);
    while(slideIndex === currentSlideIndex) {
        slideIndex = randomIntInRange(1, slides.length);
    }
    let nextSlide = slides.eq(slideIndex);
    currentSlideIndex = slideIndex;

    // move the next image up in the stack
    nextSlide.css("z-index",2);

    // Fade out the top image
    active.fadeOut(fadeOutDuration, () => {
        // Then move it back to the bottom of the pile, unhide the image, and remove the active class
        active.css('z-index',1).show().removeClass('active');

        // And move the next image to the top of the pile
        nextSlide.css('z-index',3).addClass('active');
    });
};
*/

// TODO: display loading spinner in top section until images load

let slideIndex = 1;

function showSlides() {
    let slides = document.getElementsByClassName("slide");
    for (let i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";
    }
    slideIndex++;
    if (slideIndex > slides.length) {slideIndex = 1}
    slides[slideIndex-1].style.display = "block";
    setTimeout(showSlides, 4000); // Change image every 4 seconds
}

$(function() {
    // run slide transition every 6 seconds
    //setInterval(slideTransition, 6000);
    showSlides()
});