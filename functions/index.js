const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

const nodemailer = require('nodemailer');

const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'rorycabraham@gmail.com',
        pass: 'Egghopper#1'
    }
});

let mailOptions = {
    from: 'rorycabraham@gmail.com',
    to: 'rorycabraham@yahoo.com',
    subject: 'Sending Email using Node.js',
    text: 'That was easy!'
};

/*transporter.sendMail(mailOptions, function(error, info){
    if (error) {
        console.log(error);
    } else {
        console.log('Email sent: ' + info.response);
    }
});*/