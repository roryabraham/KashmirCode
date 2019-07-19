let slideIndex = 0;
let displayState = false;
const fadeInDuration = 3000;
const fadeOutDuration = 2000;

hideSlide = (slides, callback) => {
    for(let i=0; i < slides.length; i++) {
        let currSlide = slides.eq(i);
        if(currSlide.css("display") !== "none") {
            currSlide.fadeOut(fadeOutDuration, callback);
        }
    }
};

showSlide = () => {
    let slides = $(".slide");

    // hide current slide
    if(displayState) {
        hideSlide(slides, function(){
            // then display new slide
            if(++slideIndex > slides.length) {slideIndex = 1}
            slides.eq(slideIndex - 1).fadeIn(fadeInDuration);
        });
    }
    else {
        // just display new slide
        if(++slideIndex > slides.length) {slideIndex = 1}
        slides.eq(slideIndex - 1).fadeIn(fadeInDuration);
        displayState = true;
    }

    setTimeout(showSlide, 6500);
};

$(function() {
    showSlide();
});