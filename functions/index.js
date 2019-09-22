const functions = require('firebase-functions');
const nodemailer = require('nodemailer');
const smtpTransport = require('nodemailer-smtp-transport');
const cors = require('cors')({
    origin: true
});

exports.sendEmail = functions.https.onRequest((req, res) => {
    const {name, email, message, date} = req.body;
    return cors(req, res, () => {
        let text = `<div>
                        New message from ${name || "UNKNOWN NAME"}:<br>
                        <p>
                            ${message || "MESSAGE CONTAINS NO CONTENT"}
                        </p><br>
                        Reply to ${name || "UNKNOWN NAME"} at ${email || "UNKNOWN OR INVALID EMAIL"}
                    </div>`;
        let sesAccessKey = 'rorycabraham@gmail.com';
        let sesSecretKey = 'Egghopper#1';

        const transporter = nodemailer.createTransport(smtpTransport({
            service: 'gmail',
            auth: {
                user: sesAccessKey,
                pass: sesSecretKey
            }
        }));

        const mailOptions = {
            from: 'rorycabraham@gmail.com',
            to: 'rorycabraham@yahoo.com',
            subject: `KashmirCode | New Message from ${name || "???"}` ,
            text: text,
            html: text
        };

        transporter.sendMail(mailOptions, (error, info) => {
            if(error) {
                console.log(error.message);
            }
            res.status(200).send({
                message: "success"
            });
        });
    }).catch(() => {
        res.status(500).send("error");
    });
});