//
//  SecondViewController.swift
//  SMFApp
//
//  Created by Nam Nghiem on 23/09/2018.
//  Copyright © 2018 Nam Nghiem. All rights reserved.
//

import UIKit
import Alamofire
import SafariServices
import SwiftyJSON
import Kingfisher

class SecondViewController: UIViewController ,UITableViewDelegate,UITableViewDataSource, UIBarPositioningDelegate, CalendarDelegate, ShareDelegate{
    func didPressShare(_ tag: Int) {
        Interactions.shareTextButton(text: events[tag].title + " " + events[tag].link, viewController: self)
    }
    
    func didPressAddCalendar(_ tag: Int) {
        
        let dateFormat = DateFormatter()
        dateFormat.dateFormat = "dd/MM/yyyy h:mm a"
        dateFormat.locale =  Locale(identifier: "en_US_POSIX")
        let event = events[tag]
        guard let date = dateFormat.date(from: event.time.lowercased()) else { return  }
        
        Interactions.addToCalendar(date: date, viewController: self, title: event.title, notes: event.description.replacingOccurrences(of: "<[^>]+>", with: "", options: String.CompareOptions.regularExpression, range: nil))
        
        
    }
    
    
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return events.count

    }
    private func addNavigationBar(){
        let height: CGFloat = 75
        let statusBarHeight = UIApplication.shared.statusBarFrame.height;
        let navbar = UINavigationBar(frame: CGRect(x: 0, y: statusBarHeight, width: UIScreen.main.bounds.width, height: height))
        navbar.backgroundColor = UIColor.white
        navbar.delegate = self as? UINavigationBarDelegate
        
        let navItem = UINavigationItem()
        navItem.title = "Sensor Data"
       
        //navItem.leftBarButtonItem = UIBarButtonItem(title: "Back", style: .plain, target: self, action: #selector(dismissViewController))
        
        navbar.items = [navItem]
        
        view.addSubview(navbar)
        
        self.view?.frame = CGRect(x: 0, y: height, width: UIScreen.main.bounds.width, height: (UIScreen.main.bounds.height - height))
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
     
        let cell = tableView.dequeueReusableCell(withIdentifier: "eventsId", for: indexPath) as! EventsTableViewCell
        
        let event = events[indexPath.row]
        cell.eventAuthor.text = event.author
        cell.eventDescription.text = event.description.replacingOccurrences(of: "<[^>]+>", with: "", options: String.CompareOptions.regularExpression, range: nil)
        cell.eventTime.text = event.time
        cell.eventVenue.text = event.venue
        cell.eventTitle.text = event.title
        
        cell.selectionStyle = .none
        
        if let url = URL(string: event.imageUrl){
            cell.eventImage.kf.setImage(with: url)
            //cell.eventImage.layer.cornerRadius = 16.0
        }
        cell.calendarButton.tag = indexPath.row
        cell.cellDelegate = self
        cell.shareDelegate = self

        // add code to download the image from fruit.imageURL
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let url = URL(string: events[indexPath.row].link) else { return }
        let svc = SFSafariViewController(url: url)
        present(svc, animated: true, completion: nil)
    }
    @IBOutlet weak var tableView: UITableView!
    
    
    @IBAction func calendarButton(_ sender: Any) {
    }
    var events: [Event] = []
    
    @IBAction func onInfo(_ sender: Any) {
        let alert = UIAlertController(title: "App Info", message: "App developed by Nam Nghiem for the Trinity SMF", preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        self.present(alert, animated: true, completion: nil)

    }
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        tableView.delegate = self
        self.title = "Events"
        tableView.dataSource = self
        activityIndicator.hidesWhenStopped = true
        activityIndicator.startAnimating()
        AF.request("http://trinitysmf.com/wp-json/wp/v2/posts?_embed&per_page=100&categories=7", method: .get, parameters: nil, encoding: JSONEncoding.default)
            .responseJSON { response in
                //to get JSON return value
                do{
                    guard response.data != nil else{return
                        //handle oflfine
                    }
                    let json = JSON(response.data!)
                    if json.count>0{
                    for x in (0...json.count-1){
                        
                        
                        let author = json[x]["_embedded"]["author"][0]["name"].string ?? "Author"
                        
                        let description = json[x]["content"]["rendered"].string ?? "Description"
                        
                        let time = json[x]["acf"]["time"].string ?? "Time"
                        
                        let venue = json[x]["acf"]["venue"].string ?? "Venue"
                        
                        let title = json[x]["title"]["rendered"].string ?? "Title"
                        
                        let pictureUrl = json[x]["_embedded"]["wp:featuredmedia"][0]["media_details"]["sizes"]["large"]["source_url"].string ?? "http://www.trinitysmf.com/wp-content/uploads/2018/07/educlass_series-1024x682.jpg"
                        
                        let link = json [x]["link"].string ?? "http://trinitysmf.com"
                        
                        let event = Event(title:title, author:author, venue:venue, time:time, description:description, imageUrl: pictureUrl, link:link)
                        
                        
                        self.events.append(event)
                    }
                    
                    self.tableView.reloadData()
                    self.tableView.isHidden = false
                    self.activityIndicator.stopAnimating()
                    }
                    
            }catch let parsingError {
                    print("Error", parsingError)
                }
        
    }
    
    

}
    
    
    }


extension String {
    var htmlToAttributedString: NSAttributedString? {
        guard let data = data(using: .utf8) else { return NSAttributedString() }
        do {
            return try NSAttributedString(data: data, options: [.documentType: NSAttributedString.DocumentType.html, .characterEncoding:String.Encoding.utf8.rawValue], documentAttributes: nil)
        } catch {
            return NSAttributedString()
        }
    }
    var htmlToString: String {
        return htmlToAttributedString?.string ?? ""
    }
}

