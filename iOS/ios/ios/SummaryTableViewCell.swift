//
//  SummaryTableViewCell.swift
//  ios
//
//  Created by Glenn Hartwell on 10/19/22.
//

import UIKit

class SummaryTableViewCell: UITableViewCell {

    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var img: UIImageView!
    @IBOutlet weak var cosmosView: CosmosView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        cosmosView.settings.fillMode = .precise
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
