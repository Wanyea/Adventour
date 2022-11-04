//
//  BeaconBoardTableViewCell.swift
//  ios
//
//  Created by Glenn Hartwell on 10/20/22.
//

import UIKit

class BeaconBoardTableViewCell: UITableViewCell {

    @IBOutlet weak var dateCreated: UILabel!

    @IBOutlet weak var authorProfilePic: UIImageView!
    @IBOutlet weak var likeIcon: UIImageView!
    @IBOutlet weak var nickname: UILabel!
    @IBOutlet weak var numLikes: UILabel!
    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var intro: UILabel!
    @IBOutlet weak var beaconPicture: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
