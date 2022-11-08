const admin = require('firebase-admin')
const nodemailer = require('nodemailer')
const router = require('express').Router()

router.post("/send-email-report", async (req, res, next) => {
    
    const { uid, id } = req.body

    admin.auth()
    .getUser(uid)
    .then(user => {
        console.log(process.env.SERVER_EMAIL, " ", process.env.SERVER_PASS)
        let transporter = nodemailer.createTransport({
            host: "smtp.zoho.com",
            port: 465,
            secure: true,
            auth: {
                user: process.env.SERVER_EMAIL,
                pass: process.env.SERVER_PASS
            }
        })

        let email = {
            from: process.env.SERVER_EMAIL,
            to: process.env.REPORT,
            subject: "A Beacon has been reported!",
            text: 
                "Hello admin,\n\n" + 
                "The Beacon with id: " + id + " by user: " + uid + " has been reported as violating our Terms of Service. " + 
                "Please take the time to review this Beacon and remove it if it does indeed violate our Terms of Service.\n\n" +
                "Thank you,\n" +
                "Adventour Beacon Report Team"
        }

        transporter.sendMail(email, (error, info) => {
            if (error) {
                console.log(error)
                res.json({
                    status: 400,
                    message: "Unable to send email."
                })
            } else {
                console.log("Email sent: ", info.response)
                res.json({
                    status: 200,
                    message: "Email sent successfully"
                })
            }
        })
    })
    .catch(error => {
        console.log(error)
        res.json({
            status: 401,
            message: "Unauthorized"
        })
    })
})

module.exports = router