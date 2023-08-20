//
//  DirectoryViewController.swift
//  SMFApp
//
//  Created by Nam Nghiem on 30/08/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import UIKit
import FirebaseUI
import FirebaseAuth

class DirectoryViewController: UITableViewController {

    var dataSource: FUITableViewDataSource!

    @IBOutlet var directoryTable: UITableView!
    
    var emails: [String] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let ref = Database.database().reference()
        
        directoryTable.delegate = self
        directoryTable.rowHeight = 110
        
        ref.child("users").child(Auth.auth().currentUser!.uid).observeSingleEvent(of: .value, with: {(snapshot) in
        
            let userDictionary = snapshot.value as? NSDictionary
            guard let sectorCode = userDictionary?["sector"] else {return}
            
            self.dataSource = self.directoryTable.bind(to: ref.child("users").queryOrdered(byChild: "sector").queryEqual(toValue: sectorCode), populateCell: { (tableView, indexPath, snapshot) -> UITableViewCell in
                let cell = tableView.dequeueReusableCell(withIdentifier: "directoryId", for: indexPath) as! DirectoryTableViewCell
                let current = snapshot.value as? NSDictionary
                cell.nameLabel.text = current?["name"] as? String ?? "Name"
                cell.contactLabel.text = current?["username"] as? String ?? "Email"
                cell.positionLabel.text = current?["position"] as? String ?? "Position"
                let gradYear = current?["gradYear"] as? String ?? "Graduation"
                let course = current?["course"] as? String ?? "Course"
                cell.detailsLabel.text = gradYear + " " + course
               
                return cell

            })
            

        
        }
        
        
            )
        
        
        
        // Do any additional setup after loading the view.
    }
    
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print(emails)
        let email = (dataSource.snapshot(at: indexPath.row).value) as? NSDictionary
        if let url = URL(string: "mailto:\(email!["username"] ?? "tech@trinitysmf.com")") {
            if #available(iOS 10.0, *) {
                UIApplication.shared.open(url)
            } else {
                UIApplication.shared.openURL(url)
            }
        }
        self.directoryTable.deselectRow(at: indexPath, animated: true)

        
    }
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        if dataSource != nil {
            return dataSource.items.count}
        else{
            return 0
        }
    }
    
    
    @IBAction func done(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
