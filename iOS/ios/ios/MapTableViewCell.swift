//
//  MapTableViewCell.swift
//  ios
//
//  Created by Glenn Hartwell on 10/4/22.
//

import UIKit
import MapKit

protocol MapTableViewCellDelegate: AnyObject {
    func onTouchShowButton(from cell: MapTableViewCell)
}

class MapTableViewCell: UITableViewCell {

    @IBOutlet weak var locationName: UILabel!
    @IBOutlet weak var showMapButton: UIButton!
    @IBOutlet weak var getDirectionsButton: UIButton!
    
    weak var delegate: MapTableViewCellDelegate?
    
    var lat: Double!
    var lon: Double!
    
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    @IBAction func getDirections(_ sender: Any) {
        let location = CLLocationCoordinate2D(latitude: self.lat, longitude: self.lon)
        let mapItem = MKMapItem(placemark: MKPlacemark(coordinate: location, addressDictionary: nil))
        mapItem.name = self.locationName.text
                    mapItem.openInMaps(launchOptions: [MKLaunchOptionsDirectionsModeKey: MKLaunchOptionsDirectionsModeDriving])
    }
    @IBAction func showOnMap(_ sender: Any) {
        self.delegate?.onTouchShowButton(from: self)
    }
}
