//
//  UniStreamViewController.swift
//  grpc-client
//
//  Created by Mo Lotfi on 9/1/17.
//  Copyright © 2017 MOIA GmbH. All rights reserved.
//

import UIKit
import MapKit

let kStartCoordinate = (lat: 53.557471, long: 10.003938)
let kRadius:CLLocationDistance = 200
let kAnimatoinDuration = 1.3

class MapViewController: UIViewController {

  @IBOutlet weak var mapView: MKMapView!
  private var coordinate: CLLocationCoordinate2D?
  private let homeLocation = CLLocation(latitude: kStartCoordinate.lat,
                                        longitude: kStartCoordinate.long)
  private var annotation = MKPointAnnotation()
  private var streamer: UidirectionalStreamer?

  override func viewDidLoad() {
    super.viewDidLoad()
    streamer = UidirectionalStreamer(delegate: self)
    centerMapOnLocation(location: homeLocation)
    addAnnotation()
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    navigationController?.navigationBar.isHidden = false
  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    streamer?.start()
  }

  override func viewDidDisappear(_ animated: Bool) {
    super.viewDidDisappear(animated)
  }

  func centerMapOnLocation(location: CLLocation) {
    mapView.delegate = self
    let coordinateRegion = MKCoordinateRegionMakeWithDistance(location.coordinate,
                                                              kRadius * 2.0, kRadius * 2.0)
    mapView.setRegion(coordinateRegion, animated: true)
  }

  private func addAnnotation() {
    annotation.coordinate = mapView.centerCoordinate
    mapView.addAnnotation(annotation)
  }

  func moveAnnotation(location:CLLocationCoordinate2D) {
    UIView.animate(withDuration: kAnimatoinDuration) {
      let center = CLLocationCoordinate2D(latitude: location.latitude, longitude: location.longitude)
      let region = MKCoordinateRegion(center: center, span: MKCoordinateSpan(latitudeDelta: 0.01, longitudeDelta: 0.01))

      self.mapView.setRegion(region, animated: true)
      self.annotation.coordinate = location
    }
  }
}

extension MapViewController:MKMapViewDelegate {
  func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
    guard !(annotation is MKUserLocation) else {
      return nil
    }

    let annotationIdentifier = "AnnotationIdentifier"
    var annotationView: MKAnnotationView?
    if let dequeuedAnnotationView = mapView.dequeueReusableAnnotationView(withIdentifier: annotationIdentifier) {
      annotationView = dequeuedAnnotationView
      annotationView?.annotation = annotation
    }
    else {
      annotationView = MKAnnotationView(annotation: annotation, reuseIdentifier: annotationIdentifier)
      annotationView?.rightCalloutAccessoryView = UIButton(type: .detailDisclosure)
    }
    if let annotationView = annotationView {
      annotationView.canShowCallout = false
      annotationView.image = UIImage(named: "vehicle")
      annotationView.contentMode = .scaleAspectFit
      annotationView.frame.size = CGSize(width: 60, height: 60)
    }
    return annotationView
  }
}

extension MapViewController:UniStreamDelegate {
  func streamManager(_ streamManager: UidirectionalStreamer, didUpdate location: (latitude: Double, longitude: Double)) {
    let location = CLLocationCoordinate2D(latitude: location.latitude, longitude: location.longitude)
    self.moveAnnotation(location: location)
  }
}
