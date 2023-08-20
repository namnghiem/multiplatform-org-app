//
//  DirectoryTableViewCell.swift
//  SMFApp
//
//  Created by Nam Nghiem on 30/08/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import UIKit

class DirectoryTableViewCell: UITableViewCell {

    @IBOutlet weak var detailsLabel: UILabel!
    @IBOutlet weak var positionLabel: UILabel!
    @IBOutlet weak var contactLabel: UILabel!
    @IBOutlet weak var nameLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
