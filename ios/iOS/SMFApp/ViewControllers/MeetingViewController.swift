//
//  MeetingViewController.swift
//  SMFApp
//
//  Created by Nam Nghiem on 30/08/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import UIKit
import FirebaseUI
import Firebase
class MeetingViewController: UITableViewController {

    @IBOutlet var meetingTable: UITableView!
    var dataSource: FUITableViewDataSource!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        meetingTable.delegate = self
        meetingTable.rowHeight = 110
        self.navigationItem.title = "Your Team Meetings"
        
        let ref = Database.database().reference()

    ref.child("users").child(Auth.auth().currentUser!.uid).observeSingleEvent(of: .value, with: {(snapshot) in
            
            let userDictionary = snapshot.value as? NSDictionary
            guard let sectorCode = userDictionary?["sector"] else {return}
        
        self.dataSource = self.meetingTable.bind(to: ref.child("sectors").child(sectorCode as! String).child("weekly_meetings"), populateCell: { (tableView, indexPath, snapshot) -> UITableViewCell in
                
            let cell = tableView.dequeueReusableCell(withIdentifier: "meetingId", for: indexPath) as! MeetingTableViewCell
            
            let current = snapshot.value as? NSDictionary
            cell.titleLabel.text = current?["name"] as? String ?? "Meeting"
            cell.dayLabel.text = current?["day"] as? String ?? "Day"
            cell.timeLabel.text = current?["time"] as? String ?? "00:00"
            cell.venueLabel.text = (current?["venue"] as? String ?? "Venue")
            cell.accessoryType = .disclosureIndicator

            return cell
                
            })
        
        })
        
        // Do any additional setup after loading the view.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    @IBAction func done(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        let childArray = (dataSource.snapshot(at: indexPath.row).value) as! [String:Any]
        
        let current = Meeting(day: childArray["day"] as! String, time: childArray["time"] as! String, venue: childArray["venue"] as! String)

        let currentDayArray = current.time.components(separatedBy: ":")

        let calendar = Calendar(identifier: .gregorian)
        let weekdayInt = Interactions.weekdays[current.day]
        let currentComponent = DateComponents(calendar: calendar, hour:Int(currentDayArray[0]), minute:Int(currentDayArray[1]), weekday:weekdayInt)
        
        guard let nextDay = calendar.nextDate(after: Date(), matching: currentComponent, matchingPolicy: .nextTime) else { return }
        
        Interactions.addToCalendar(date: nextDay, viewController: self, title: childArray["name"] as! String, notes: "The meeting will be held at: "+current.venue)
        
        self.meetingTable.deselectRow(at: indexPath, animated: true)
    
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
            
            if dataSource != nil {
                return dataSource.items.count}
            else{
                return 0
            }
       }
}
