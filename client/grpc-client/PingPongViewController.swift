//
//  PingPongViewController.swift
//  grpc-client
//
//  Created by Markus Jura on 05.09.17.
//  Copyright © 2017 MOIA GmbH. All rights reserved.
//

import UIKit



class PingPongViewController: UIViewController {
  @IBOutlet weak var pongView: UILabel! {
    didSet{
      pongView.alpha = 0
    }
  }

  private var streamer: BidirectionalStreamer?
  override func viewDidLoad() {
    super.viewDidLoad()
    self.streamer = BidirectionalStreamer(delegate: self)
  }

  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
  }

  @IBAction func startPressed(_ sender: Any) {
    streamer?.start()
  }

  func flashPongView() {
    UIView.animate(withDuration: 0.2, animations: {
      self.pongView.alpha = 1
    }) { (finished) in
      self.pongView.alpha = 0
    }
  }
}


extension PingPongViewController:BiDirectionStreamDelegate {
  func bidirectionStreamManager(_ streamManager: BidirectionalStreamer, didReceive pong: String) {
    self.flashPongView()
  }
}
