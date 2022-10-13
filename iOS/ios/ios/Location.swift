//
//  Location.swift
//  ios
//
//  Created by Glenn Hartwell on 10/4/22.
//

import Foundation

class Location {
    
    private var fsq_id: String
    private var name: String
    private var description: String
    private var tel: String
    private var website: String
    private var rating: Double
    private var popularity: Double
    private var price: Double
    
    init(fsq_id: String, name: String, description: String, tel: String, website: String, rating: Double, popularity: Double, price: Double) {
        self.fsq_id = fsq_id
        self.name = name
        self.description = description
        self.tel = tel
        self.website = website
        self.rating = rating
        self.popularity = popularity
        self.price = price
    }
    
    func getFsqId() -> String {return fsq_id}
    func getName() -> String {return name}
    func getDescription() -> String {return description}
    func getTel() -> String {return tel}
    func getWebsite() -> String {return website}
    func getRating() -> Double {return rating}
    func getPopularity() -> Double {return popularity}
    func getPrice() -> Double {return price}
}
