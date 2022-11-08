
const admin = require('firebase-admin')
const router = require('express').Router()
const foursquare = require('api')('@fsq-docs/v1.0#3qzzviakl47e7mgp');

require("dotenv").config(); 

module.exports = router
const key = process.env.FOURSQUARE_API_KEY
const fields = 'fsq_id%2Cname%2Cdescription%2Ctel%2Cwebsite%2Crating%2Cpopularity%2Cprice%2Cdistance%2Chours%2Ccategories%2Cgeocodes%2Cphotos%2Clocation%2Cfeatures'
const limit = 50
const sort = "distance"
const exclude_all_chains = true
const open_now = true
router.post('/get-adventour-place', async (req, res, next) => {

    const { uid, ll, radius, categories, excludes } = req.body


    console.log("Body")
    console.log("===============")
    console.log(req.body)
    console.log("===============")

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
                limit: limit,
                exclude_all_chains: exclude_all_chains,
                open_now: open_now
            })
            .then(response => {

                // Remove excluded places
                if (excludes) {
                    console.log(response.results)
                    response.results = response.results.filter((item, index, array) => {
                        return !excludes.includes(item.fsq_id)
                    })
                    
                }
                // If foursquare cannot return any places, or we have excluded all of the places
                if (response.results.length == 0) {
                    let ret = {
                        status: 200,
                        error: "Unable to return search results. Please change the search metrics and try again.",
                        data: []
                    }

                    res.status(200).json(ret)
                }
            
                let hasRating = response.results.filter((item, index, array) => {
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
                
                let i = Math.floor(Math.random() * final.length)

                // console.log("The final choice!.\n\n")
                // console.log(final[i])

                let ret = {
                    status: 200,
                    error: null,
                    data: final[i]
                }

                res.status(200).json(ret)
            })
            .catch(error => {
                let ret = {
                    status: 400,
                    error: error,
                    data: []
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
                data: []
            }
            console.log(error)
            res.status(401).json(ret)
        });
    

    
})