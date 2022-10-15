//
//  MapTableViewCell.swift
//  ios
//
//  Created by Glenn Hartwell on 10/4/22.
//

import UIKit

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

    @IBAction func showOnMap(_ sender: Any) {
        self.delegate?.onTouchShowButton(from: self)
    }
}
