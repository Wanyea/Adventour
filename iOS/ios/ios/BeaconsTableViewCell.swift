//
//  BeaconsTableViewCell.swift
//  ios
//
//  Created by Glenn Hartwell on 10/15/22.
//

import UIKit

class BeaconsTableViewCell: UITableViewCell {

    @IBOutlet weak var dateCreated: UILabel!
    @IBOutlet weak var location1: UILabel!
    @IBOutlet weak var location2: UILabel!
    @IBOutlet weak var location3: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
//        let bgColorView = UIView()
//        bgColorView.backgroundColor = UIColor.clear
//        self.selectedBackgroundView = bgColorView
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }


}
