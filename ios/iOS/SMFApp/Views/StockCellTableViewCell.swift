//
//  StockCellTableViewCell.swift
//  SMFApp
//
//  Created by Nam Nghiem on 20/08/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import UIKit

class StockCellTableViewCell: UITableViewCell {

    @IBOutlet weak var stockName: UILabel!
    @IBOutlet weak var stockTicker: UILabel!
    @IBOutlet weak var stockNumber: UILabel!
    @IBOutlet weak var stockBuyPrice: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
