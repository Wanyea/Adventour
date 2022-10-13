
const router = require('express').Router()
const foursquare = require('api')('@fsq-docs/v1.0#3qzzviakl47e7mgp');

require("dotenv").config(); 

module.exports = router
const key = process.env.FOURSQUARE_API_KEY

router.post('/get-place-by-id', async (req, res, next) => {

    const {fsq_id} = req.body

    foursquare.auth(key);
    foursquare.placeDetails({
        fields: 'name%2Clocation%2Cdescription%2Ctel%2Cwebsite%2Crating%2Cpopularity%2Cprice%2Cphotos',
        fsq_id: fsq_id
    })
    .then(foursquareResult => {

        ret = {
            status: 200,
            error: "",
            result: foursquareResult
        }

        res.status(200).json(ret)
    })
    .catch(err => {
        ret = {
            status: 400,
            error: err,
            result: ""
        }
        console.error(err)
        res.status(400).json(ret)
    });
})