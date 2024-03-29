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
    @IBOutlet weak var descriptionTextView: BeaconPostTextView!
    
    @IBOutlet weak var imageScroll: UIScrollView!
    @IBOutlet weak var cosmosView: CosmosView!
    @IBOutlet weak var addressLabel: UIButton!
    @IBOutlet weak var locationPhoto1: UIImageView!
    @IBOutlet weak var locationPhoto2: UIImageView!
    @IBOutlet weak var locationPhoto3: UIImageView!
    
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
        self.imageScroll?.contentSize.height = 1.0
        self.cosmosView.settings.fillMode = .precise
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
