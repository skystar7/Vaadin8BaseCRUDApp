package com.tigerfixonline.crud.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Georgia {

	private static final String[] cities = { "Abbeville", "Acworth", "Adairsville", "Adel", "Adrian", "Ailey", "Alamo",
			"Alapaha", "Albany", "Aldora", "Allenhurst", "Allentown", "Alma", "Alpharetta", "Alston", "Alto", "Ambrose",
			"Americus", "Andersonville", "Arabi", "Aragon", "Arcade", "Argyle", "Arlington", "Ashburn", "Athens",
			"Atlanta", "Attapulgus", "Auburn", "Augusta", "Austell", "Avera", "Avondale Estates", "Baconton",
			"Bainbridge", "Baldwin", "Ball Ground", "Barnesville", "Bartow", "Barwick", "Baxley", "Berkeley Lake",
			"Berlin", "Bethlehem", "Bishop", "Blackshear", "Blairsville", "Blakely", "Bloomingdale", "Blue Ridge",
			"Bluffton", "Blythe", "Bogart", "Boston", "Bostwick", "Bowdon", "Bowersville", "Bowman", "Braselton",
			"Braswell", "Bremen", "Brinson", "Bronwood", "Brookhaven", "Brooklet", "Brooks", "Broxton", "Brunswick",
			"Buchanan", "Buckhead", "Buena Vista", "Buford", "Butler", "Byromville", "Byron", "Cadwell", "Cairo",
			"Calhoun", "Camak", "Camilla", "Canon", "Canton", "Carl", "Carlton", "Carnesville", "Carrollton",
			"Cartersville", "Cave Spring", "Cecil", "Cedartown", "Centerville", "Centralhatchee", "Chamblee",
			"Chatsworth", "Chattahoochee Hills", "Chauncey", "Chester", "Chickamauga", "Clarkesville", "Clarkston",
			"Claxton", "Clayton", "Clermont", "Cleveland", "Climax", "Cobbtown", "Cochran", "Cohutta", "Colbert",
			"College Park", "Collins", "Colquitt", "Columbus", "Comer", "Commerce", "Concord", "Conyers", "Coolidge",
			"Cordele", "Cornelia", "Covington", "Crawford", "Crawfordville", "Culloden", "Cumming", "Cusseta",
			"Cuthbert", "Dacula", "Dahlonega", "Dallas", "Dalton", "Damascus", "Danielsville", "Danville", "Darien",
			"Dasher", "Davisboro", "Dawson", "Dawsonville", "De Soto", "Dearing", "Decatur", "Deepstep", "Demorest",
			"Dexter", "Dillard", "Doerun", "Donalsonville", "Dooling", "Doraville", "Douglas", "Douglasville",
			"Du Pont", "Dublin", "Dudley", "Duluth", "Dunwoody", "East Dublin", "East Ellijay", "East Point", "Eastman",
			"Eatonton", "Edge Hill", "Edison", "Elberton", "Ellaville", "Ellenton", "Ellijay", "Emerson", "Enigma",
			"Ephesus", "Eton", "Euharlee", "Fairburn", "Fairmount", "Fargo", "Fayetteville", "Fitzgerald", "Flemington",
			"Flovilla", "Flowery Branch", "Folkston", "Forest Park", "Forsyth", "Fort Gaines", "Fort Oglethorpe",
			"Fort Valley", "Franklin", "Franklin Springs", "Funston", "Gainesville", "Garden City", "Garfield", "Gay",
			"Geneva", "Georgetown", "Gibson", "Gillsville", "Girard", "Glennville", "Glenwood", "Good Hope", "Gordon",
			"Graham", "Grantville", "Gray", "Grayson", "Greensboro", "Greenville", "Griffin", "Grovetown", "Gumbranch",
			"Guyton", "Hagan", "Hahira", "Hamilton", "Hampton", "Hapeville", "Haralson", "Harlem", "Harrison",
			"Hartwell", "Hawkinsville", "Hazlehurst", "Helen", "Hephzibah", "Hiawassee", "Higgston", "Hiltonia",
			"Hinesville", "Hiram", "Hoboken", "Hogansville", "Holly Springs", "Homeland", "Homer", "Homerville",
			"Hoschton", "Hull", "Ideal", "Ila", "Iron City", "Irwinton", "Ivey", "Jackson", "Jacksonville", "Jakin",
			"Jasper", "Jefferson", "Jeffersonville", "Jenkinsburg", "Jersey", "Jesup", "Johns Creek", "Jonesboro",
			"Junction City", "Kennesaw", "Keysville", "Kingsland", "Kingston", "Kite", "LaFayette", "LaGrange",
			"Lake City", "Lake Park", "Lakeland", "Lavonia", "Lawrenceville", "Leary", "Leesburg", "Lenox", "Leslie",
			"Lexington", "Lilburn", "Lilly", "Lincolnton", "Lithonia", "Locust Grove", "Loganville", "Lone Oak",
			"Lookout Mountain", "Louisville", "Lovejoy", "Ludowici", "Lula", "Lumber City", "Lumpkin", "Luthersville",
			"Lyerly", "Lyons", "Macon", "Madison", "Manassas", "Manchester", "Mansfield", "Marietta", "Marshallville",
			"Martin", "Maxeys", "Maysville", "McCaysville", "McDonough", "McIntyre", "McRae-Helena", "Meansville",
			"Meigs", "Menlo", "Metter", "Midville", "Midway", "Milan", "Milledgeville", "Millen", "Milner", "Milton",
			"Mitchell", "Molena", "Monroe", "Montezuma", "Monticello", "Montrose", "Moreland", "Morgan", "Morganton",
			"Morrow", "Morven", "Moultrie", "Mount Airy", "Mount Vernon", "Mount Zion", "Mountain City",
			"Mountain Park", "Nahunta", "Nashville", "Nelson", "Newborn", "Newington", "Newnan", "Newton", "Nicholls",
			"Nicholson", "Norcross", "Norman Park", "North High Shoals", "Norwood", "Oak Park", "Oakwood", "Ochlocknee",
			"Ocilla", "Oconee", "Odum", "Offerman", "Oglethorpe", "Oliver", "Omega", "Orchard Hill", "Oxford",
			"Palmetto", "Parrott", "Patterson", "Pavo", "Payne City", "Peachtree City", "Peachtree Corners", "Pearson",
			"Pelham", "Pembroke", "Pendergrass", "Perry", "Pine Lake", "Pine Mountain", "Pinehurst", "Pineview",
			"Pitts", "Plains", "Plainville", "Pooler", "Port Wentworth", "Portal", "Porterdale", "Poulan",
			"Powder Springs", "Preston", "Pulaski", "Quitman", "Ranger", "Ray City", "Rayle", "Rebecca", "Register",
			"Reidsville", "Remerton", "Rentz", "Resaca", "Reynolds", "Rhine", "Riceboro", "Richland", "Richmond Hill",
			"Riddleville", "Rincon", "Ringgold", "Riverdale", "Riverside", "Roberta", "Rochelle", "Rockmart",
			"Rocky Ford", "Rome", "Rossville", "Roswell", "Royston", "Rutledge", "Sale City", "Sandersville",
			"Sandy Springs", "Santa Claus", "Sardis", "Sasser", "Savannah", "Scotland", "Screven", "Senoia",
			"Shady Dale", "Sharon", "Sharpsburg", "Shellman", "Shiloh", "Siloam", "Sky Valley", "Smithville", "Smyrna",
			"Snellville", "Social Circle", "Soperton", "Sparks", "Sparta", "Springfield", "St. Marys", "Stapleton",
			"Statenville", "Statesboro", "Statham", "Stillmore", "Stockbridge", "Stone Mountain", "Sugar Hill",
			"Summerville", "Sumner", "Surrency", "Suwanee", "Swainsboro", "Sycamore", "Sylvania", "Sylvester",
			"Talbotton", "Talking Rock", "Tallapoosa", "Tallulah Falls", "Talmo", "Tarrytown", "Taylorsville", "Temple",
			"Tennille", "Thomaston", "Thomasville", "Thomson", "Thunderbolt", "Tifton", "Tignall", "Toccoa",
			"Toomsboro", "Trenton", "Trion", "Tunnel Hill", "Turin", "Twin City", "Ty Ty", "Tybee Island", "Tyrone",
			"Unadilla", "Union City", "Union Point", "Uvalda", "Valdosta", "Varnell", "Vernonburg", "Vidalia", "Vienna",
			"Villa Rica", "Waco", "Wadley", "Waleska", "Walnut Grove", "Walthourville", "Warm Springs", "Warner Robins",
			"Warrenton", "Warwick", "Washington", "Watkinsville", "Waverly Hall", "Waycross", "Waynesboro",
			"West Point", "Whigham", "White", "White Plains", "Whitesburg", "Willacoochee", "Williamson", "Winder",
			"Winterville", "Woodbine", "Woodbury", "Woodland", "Woodstock", "Woodville", "Woolsey", "Wrens",
			"Wrightsville", "Yatesville", "Young Harris", "Zebulon" };

	public static Collection<String> getCities() {
		List<String> list = Arrays.asList(cities);
		Collections.sort(list);
		return list;
	}

}
