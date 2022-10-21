//
//  BeaconPostTableViewCell.swift
//  ios
//
//  Created by Glenn Hartwell on 10/20/22.
//

import UIKit
import CoreLocation
import MapKit

class BeaconPostTableViewCell: UITableViewCell {

    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    
    @IBOutlet weak var imageScroll: UIScrollView!
    @IBOutlet weak var ratingLabel: UILabel!
    @IBOutlet weak var addressLabel: UIButton!
    
    @IBAction func openAddress(_ sender: Any) {
        if let myAddress = self.addressLabel.titleLabel?.text as? String {
            let geoCoder = CLGeocoder()
            geoCoder.geocodeAddressString(myAddress) { (placemarks, error) in
                guard let placemarks = placemarks?.first else { return }
                let location = placemarks.location?.coordinate ?? CLLocationCoordinate2D()
                let mapItem = MKMapItem(placemark: MKPlacemark(coordinate: location, addressDictionary: nil))
                mapItem.name = self.nameLabel.text
                            mapItem.openInMaps(launchOptions: [MKLaunchOptionsDirectionsModeKey: MKLaunchOptionsDirectionsModeDriving])
            }
        } else {
            print("Cannot open address")
        }
        
    }
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.nameLabel?.adjustsFontSizeToFitWidth = true
        self.nameLabel?.minimumScaleFactor = 0.75
        self.imageScroll.contentSize.height = 1.0
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
