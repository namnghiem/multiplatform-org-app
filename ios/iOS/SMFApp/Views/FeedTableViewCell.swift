//
//  FeedTableViewCell.swift
//  SMFApp
//
//  Created by Nam Nghiem on 29/08/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import UIKit

class FeedTableViewCell: UITableViewCell {
    
    @IBOutlet weak var postLabel: UILabel!
    @IBOutlet weak var authorLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
        
    @IBOutlet weak var filterMask: UIView!
    @IBOutlet weak var contentCell: UIView!
    
    @IBOutlet weak var filterButton: UIButton!
    var delegate: FilterDelegate?
   
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
  
    @IBAction func didPress(_ sender: UIButton) {
        delegate?.didPressFilter(sender.tag)
    }
    
}
