//
//  BeaconBoardController.swift
//  ios
//
//  Created by Cassidy Moore on 10/20/22.
//

import Foundation
import GooglePlaces

class BeaconBoardViewController: UIViewController {
    
    var temp = ""
    var lon: Double!
    var lat: Double!
    
    
    
 
    
    // Card Outlets
    private var placesClient: GMSPlacesClient!
    @IBOutlet weak var searchBar: UISearchBar!
    @IBOutlet weak var locationPhoto: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var websiteClickable: UILabel!
    @IBOutlet weak var phoneClickable: UILabel!
    @IBOutlet weak var distanceLabel: UILabel!
    @IBOutlet weak var priceLabel: UILabel!
    @IBOutlet weak var phoneLabel: UILabel!
    @IBOutlet weak var websiteLabel: UILabel!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var errorMessageLabel: UILabel!
    
    // Button Outlets
    @IBOutlet weak var notNow: UIButton!
    
    var fsq_id: String!
    var ids: [String]? = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
               
        
        
        
        //Adding the bordercolor and corner radius on the not now button. Actual border is in its runtime attributes.
        notNow?.layer.borderColor = UIColor.red.cgColor
        notNow?.layer.cornerRadius = 15
        // Do any additional setup after loading the view.
        placesClient = GMSPlacesClient.shared()
        searchBar?.text = self.temp
    }
    
    @IBAction func getCurrentPlace(_ sender: Any){
        let autocompleteController = GMSAutocompleteViewController()
        autocompleteController.delegate = self
        let fields: GMSPlaceField = GMSPlaceField(rawValue: UInt(GMSPlaceField.coordinate.rawValue) |  UInt(GMSPlaceField.name.rawValue) | UInt(GMSPlaceField.formattedAddress.rawValue) | UInt(GMSPlaceField.placeID.rawValue))
        
        autocompleteController.placeFields = fields
        
        
        let filter = GMSAutocompleteFilter()
        filter.countries = ["us"]
        filter.type = .city
        
        autocompleteController.autocompleteFilter = filter
        present(autocompleteController,animated: true, completion: nil)
        
    }
    @IBAction func updateSearch()
    {
        searchBar?.text = self.temp
    }
    
    
    
   
        
}
    
 
extension BeaconBoardViewController: GMSAutocompleteViewControllerDelegate {

    
    
  // Handle the user's selection.
  func viewController(_ viewController: GMSAutocompleteViewController, didAutocompleteWith place: GMSPlace) {
    print("Place name: \(place.name)")
    print("Place ID: \(place.placeID)")
    print("Place attributions: \(place.attributions)")
    print("Place address: \(place.formattedAddress))")
    self.lat = place.coordinate.latitude
    self.lon = place.coordinate.longitude
    print("Place lat: \(self.lat)")
    print("Place lon: \(self.lon)")
    self.temp = place.formattedAddress!
    updateSearch()
    dismiss(animated: true, completion: nil)
    
  }

  func viewController(_ viewController: GMSAutocompleteViewController, didFailAutocompleteWithError error: Error) {
    // TODO: handle the error.
    print("Error: ", error.localizedDescription)
  }

  // User canceled the operation.
  func wasCancelled(_ viewController: GMSAutocompleteViewController) {
    dismiss(animated: true, completion: nil)
  }

  // Turn the network activity indicator on and off again.
  func didRequestAutocompletePredictions(_ viewController: GMSAutocompleteViewController) {
    UIApplication.shared.isNetworkActivityIndicatorVisible = true
  }

  func didUpdateAutocompletePredictions(_ viewController: GMSAutocompleteViewController) {
    UIApplication.shared.isNetworkActivityIndicatorVisible = false
  }

}
