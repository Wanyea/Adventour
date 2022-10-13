
var admin = require("firebase-admin");

const router = require('express').Router()
const foursquare = require('api')('@fsq-docs/v1.0#3qzzviakl47e7mgp');

require("dotenv").config(); 

module.exports = router
const key = process.env.FOURSQUARE_API_KEY


router.post('/get-foursquare-places', async (req, res, next) => {

    const { uid, ids } = req.body

    

    admin.auth()
        .getUser(uid)
        .then(userRecord => {
            console.log(`Successfully fetched user data: ${userRecord.toJSON()}`)

             console.log("Body")
             console.log("===============")
             console.log(req.body)
             console.log("===============")
             console.log("ids")
             console.log("===============")
             console.log(ids)
             console.log("===============")


            foursquare.auth(key);
            
            Promise.all(ids.map(item => {
                return foursquare.placeDetails({
                    fields: 'fsq_id%2Cname%2Clocation%2Cdescription%2Ctel%2Cwebsite%2Crating%2Cpopularity%2Cprice%2Cphotos',
                    fsq_id: item
                })
            }))
            .then(results => {
                 console.log("Results")
                 console.log("===============")
                 console.log(results)
                 console.log("===============")
                
                var ret = {
                    status: 200,
                    error: "",
                    results: results
                }
                
                res.status(200).json(ret)
                
            })
            .catch(error => {
                console.log(error)
                var ret = {
                    status: 400,
                    error: error,
                    results: []
                }
                res.status(400).json(ret)
            })
        })
        .catch(error => {
            console.log("Invalid user: access denied.")
            console.log(error)
            var ret = {
                status: 401,
                error: error,
                results: []
            }
            res.status(401).json(ret)
        })
})
