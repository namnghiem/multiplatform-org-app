//
//  MeetingTableViewCell.swift
//  SMFApp
//
//  Created by Nam Nghiem on 30/08/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import UIKit

class MeetingTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var dayLabel: UILabel!
    @IBOutlet weak var venueLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
