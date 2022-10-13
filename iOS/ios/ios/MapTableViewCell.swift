//
//  MapTableViewCell.swift
//  ios
//
//  Created by Glenn Hartwell on 10/4/22.
//

import UIKit

class MapTableViewCell: UITableViewCell {

    @IBOutlet weak var locationName: UILabel!
    @IBOutlet weak var showMapButton: UIButton!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
