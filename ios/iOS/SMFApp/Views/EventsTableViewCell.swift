//
//  EventsTableViewCell.swift
//  SMFApp
//
//  Created by Nam Nghiem on 27/08/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//
import UIKit

class EventsTableViewCell: UITableViewCell {
    
    @IBOutlet weak var eventTitle: UILabel!
    @IBOutlet weak var eventAuthor: UILabel!
    @IBOutlet weak var eventVenue: UILabel!
    
    @IBOutlet weak var eventTime: UILabel!
    @IBOutlet weak var eventDescription: UILabel!
    @IBOutlet weak var eventImage: UIImageView!
    
    @IBOutlet weak var calendarButton: UIButton!
    @IBOutlet weak var tableViewCell: UIView!
    @IBOutlet weak var shareButton: UIButton!
    
    var cellDelegate: CalendarDelegate?
    var shareDelegate: ShareDelegate?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        tableViewCell.layer.cornerRadius = 17.0
    }
    
    @IBAction func calendarAction(_ sender: UIButton) {
        cellDelegate?.didPressAddCalendar(sender.tag)
    }
    
    @IBAction func shareAction(_ sender: UIButton) {
        shareDelegate?.didPressShare(sender.tag)
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }
    
}
