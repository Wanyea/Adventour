
const admin = require('firebase-admin')
const router = require('express').Router()
const foursquare = require('api')('@fsq-docs/v1.0#3qzzviakl47e7mgp');

require("dotenv").config(); 

module.exports = router
const key = process.env.FOURSQUARE_API_KEY
const fields = 'fsq_id%2Cname%2Cdescription%2Ctel%2Cwebsite%2Crating%2Cpopularity%2Cprice%2Cdistance%2Chours%2Ccategories%2Cgeocodes%2Cphotos%2Clocation'
const limit = 50
const sort = "distance"
router.post('/get-adventour-place', async (req, res, next) => {

    const { uid, ll, radius, categories } = req.body


    admin.auth()
        .getUser(uid)
        .then(userRecord => {
            // See the UserRecord reference doc for the contents of userRecord.
            console.log(`Successfully fetched user data: ${userRecord.toJSON()}`);

            foursquare.auth(key);
            foursquare.placeSearch({
                ll: ll,
                radius: radius,
                fields: fields,
                categories: categories,
                sort: sort,
                limit: limit
            })
            .then(foursquareResult => {
            
                let openNow = foursquareResult.results.filter((item, index, array) => {
                    return item.hours.open_now == true
                })
                let hasRating = openNow.filter((item, index, array) => {
                    return item.rating != null
                })
                let distanceSplit = hasRating.slice(0, 40)
                
                let highRating = distanceSplit.sort((a, b) => {
                    return b.rating - a.rating
                })
                let ratingSplit = highRating.slice(0, 25)
                let mostPopular = ratingSplit.sort((a, b) => {
                    return b.popularity - a.popularity
                })
                let final = mostPopular.slice(0,10)
                
                let i = Math.floor(Math.random() * 10)

                console.log("The final choice!.\n\n")
                console.log(final[i])

                let ret = {
                    status: 200,
                    error: "",
                    data: final[i]
                }

                res.status(200).json(ret)
            })
            .catch(error => {
                let ret = {
                    status: 400,
                    error: error,
                    data: null
                }
                console.log("Bad request to Foursquare API: \n" + error)
                res.status(400).json(ret)
            });
        })
        .catch(error => {
            console.log("There is no matching Firebase uid or some other error has occurred.\n", error);
            let ret = {
                status: 401,
                error: error,
                data: null
            }
            console.log(error)
            res.status(401).json(ret)
        });
    

    
})

// function filterOpen(array) {
//     return array.filter((value, index, array))
// }