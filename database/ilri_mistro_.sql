-- phpMyAdmin SQL Dump
-- version 3.5.8.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Oct 17, 2013 at 02:30 PM
-- Server version: 5.5.32-0ubuntu0.13.04.1
-- PHP Version: 5.4.9-4ubuntu2.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `ilri_mistro`
--

-- --------------------------------------------------------

--
-- Table structure for table `breed`
--

CREATE TABLE IF NOT EXISTS `breed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=21 ;

--
-- Dumping data for table `breed`
--

INSERT INTO `breed` (`id`, `name`) VALUES
(4, 'Aberdeen Angus'),
(10, 'Ankole'),
(1, 'Ayrshire'),
(2, 'Boran'),
(12, 'Brahman'),
(19, 'Brown Swiss'),
(3, 'Charolais'),
(18, 'Fleckviah'),
(6, 'Friesian'),
(7, 'Guernsey'),
(8, 'Hereford'),
(9, 'Jersey'),
(11, 'Limousine'),
(17, 'Nganda'),
(14, 'Piemontese'),
(15, 'Red Poll'),
(16, 'Sahiwal'),
(13, 'Santa Getrude'),
(5, 'Simmental'),
(20, 'Zebu');

-- --------------------------------------------------------

--
-- Table structure for table `cause_of_death`
--

CREATE TABLE IF NOT EXISTS `cause_of_death` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `cause_of_death`
--

INSERT INTO `cause_of_death` (`id`, `name`) VALUES
(1, 'Natural Causes'),
(2, 'Sickness'),
(3, 'Injury');

-- --------------------------------------------------------

--
-- Table structure for table `country`
--

CREATE TABLE IF NOT EXISTS `country` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_estonian_ci AUTO_INCREMENT=197 ;

--
-- Dumping data for table `country`
--

INSERT INTO `country` (`id`, `name`) VALUES
(4, 'Afghanistan'),
(5, 'Albania'),
(6, 'Algeria'),
(7, 'Andorra'),
(8, 'Angola'),
(9, 'Antigua and Deps'),
(10, 'Argentina'),
(11, 'Armenia'),
(12, 'Australia'),
(13, 'Austria'),
(14, 'Azerbaijan'),
(15, 'Bahamas'),
(16, 'Bahrain'),
(17, 'Bangladesh'),
(18, 'Barbados'),
(19, 'Belarus'),
(20, 'Belgium'),
(21, 'Belize'),
(22, 'Benin'),
(23, 'Bhutan'),
(24, 'Bolivia'),
(25, 'Bosnia Herzegovina'),
(26, 'Botswana'),
(27, 'Brazil'),
(28, 'Brunei'),
(29, 'Bulgaria'),
(30, 'Burkina'),
(31, 'Burundi'),
(32, 'Cambodia'),
(33, 'Cameroon'),
(34, 'Canada'),
(35, 'Cape Verde'),
(36, 'Central African Rep'),
(37, 'Chad'),
(38, 'Chile'),
(39, 'China'),
(40, 'Colombia'),
(41, 'Comoros'),
(42, 'Congo'),
(43, 'Congo (Democratic Rep)'),
(44, 'Costa Rica'),
(45, 'Croatia'),
(46, 'Cuba'),
(47, 'Cyprus'),
(48, 'Czech Republic'),
(49, 'Denmark'),
(50, 'Djibouti'),
(51, 'Dominica'),
(52, 'Dominican Republic'),
(53, 'East Timor'),
(54, 'Ecuador'),
(55, 'Egypt'),
(56, 'El Salvador'),
(57, 'Equatorial Guinea'),
(58, 'Eritrea'),
(59, 'Estonia'),
(60, 'Ethiopia'),
(61, 'Fiji'),
(62, 'Finland'),
(63, 'France'),
(64, 'Gabon'),
(65, 'Gambia'),
(66, 'Georgia'),
(67, 'Germany'),
(68, 'Ghana'),
(69, 'Greece'),
(70, 'Grenada'),
(71, 'Guatemala'),
(72, 'Guinea'),
(73, 'Guinea-Bissau'),
(74, 'Guyana'),
(75, 'Haiti'),
(76, 'Honduras'),
(77, 'Hungary'),
(78, 'Iceland'),
(79, 'India'),
(80, 'Indonesia'),
(81, 'Iran'),
(82, 'Iraq'),
(83, 'Ireland (Republic)'),
(84, 'Israel'),
(85, 'Italy'),
(86, 'Ivory Coast'),
(87, 'Jamaica'),
(88, 'Japan'),
(89, 'Jordan'),
(90, 'Kazakhstan'),
(1, 'Kenya'),
(91, 'Kiribati'),
(92, 'Korea North'),
(93, 'Korea South'),
(94, 'Kosovo'),
(95, 'Kuwait'),
(96, 'Kyrgyzstan'),
(97, 'Laos'),
(98, 'Latvia'),
(99, 'Lebanon'),
(100, 'Lesotho'),
(101, 'Liberia'),
(102, 'Libya'),
(103, 'Liechtenstein'),
(104, 'Lithuania'),
(105, 'Luxembourg'),
(106, 'Macedonia'),
(107, 'Madagascar'),
(108, 'Malawi'),
(109, 'Malaysia'),
(110, 'Maldives'),
(111, 'Mali'),
(112, 'Malta'),
(113, 'Marshall Islands'),
(114, 'Mauritania'),
(115, 'Mauritius'),
(116, 'Mexico'),
(117, 'Micronesia'),
(118, 'Moldova'),
(119, 'Monaco'),
(120, 'Mongolia'),
(121, 'Montenegro'),
(122, 'Morocco'),
(123, 'Mozambique'),
(124, 'Myanmar (Burma)'),
(125, 'Namibia'),
(126, 'Nauru'),
(127, 'Nepal'),
(128, 'Netherlands'),
(129, 'New Zealand'),
(130, 'Nicaragua'),
(131, 'Niger'),
(132, 'Nigeria'),
(133, 'Norway'),
(134, 'Oman'),
(135, 'Pakistan'),
(136, 'Palau'),
(137, 'Panama'),
(138, 'Papua New Guinea'),
(139, 'Paraguay'),
(140, 'Peru'),
(141, 'Philippines'),
(142, 'Poland'),
(143, 'Portugal'),
(144, 'Qatar'),
(145, 'Romania'),
(146, 'Russian Federation'),
(147, 'Rwanda'),
(150, 'Saint Vincent and the Grenadines'),
(151, 'Samoa'),
(152, 'San Marino'),
(153, 'Sao Tome and Principe'),
(154, 'Saudi Arabia'),
(155, 'Senegal'),
(156, 'Serbia'),
(157, 'Seychelles'),
(158, 'Sierra Leone'),
(159, 'Singapore'),
(160, 'Slovakia'),
(161, 'Slovenia'),
(162, 'Solomon Islands'),
(163, 'Somalia'),
(164, 'South Africa'),
(165, 'South Sudan'),
(166, 'Spain'),
(167, 'Sri Lanka'),
(148, 'St Kitts and Nevis'),
(149, 'St Lucia'),
(168, 'Sudan'),
(169, 'Suriname'),
(170, 'Swaziland'),
(171, 'Sweden'),
(172, 'Switzerland'),
(173, 'Syria'),
(174, 'Taiwan'),
(175, 'Tajikistan'),
(2, 'Tanzania'),
(176, 'Thailand'),
(177, 'Togo'),
(178, 'Tonga'),
(179, 'Trinidad and Tobago'),
(180, 'Tunisia'),
(181, 'Turkey'),
(182, 'Turkmenistan'),
(183, 'Tuvalu'),
(3, 'Uganda'),
(184, 'Ukraine'),
(185, 'United Arab Emirates'),
(186, 'United Kingdom'),
(187, 'United States'),
(188, 'Uruguay'),
(189, 'Uzbekistan'),
(190, 'Vanuatu'),
(191, 'Vatican City'),
(192, 'Venezuela'),
(193, 'Vietnam'),
(194, 'Yemen'),
(195, 'Zambia'),
(196, 'Zimbabwe');

-- --------------------------------------------------------

--
-- Table structure for table `cow`
--

CREATE TABLE IF NOT EXISTS `cow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `farmer_id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `ear_tag_number` varchar(255) NOT NULL,
  `date_of_birth` datetime DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `age_type` enum('Years','Weeks','Days') DEFAULT NULL,
  `sex` enum('Female','Male') NOT NULL,
  `sire_id` int(11) DEFAULT NULL,
  `dam_id` int(11) DEFAULT NULL,
  `straw_id` int(11) DEFAULT NULL,
  `embryo_id` int(11) DEFAULT NULL,
  `date_added` datetime NOT NULL,
  `service_type` enum('Bull','AI','ET') DEFAULT NULL,
  `country_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `farmer_id_2` (`farmer_id`,`name`),
  KEY `farmer_id` (`farmer_id`),
  KEY `sire_id` (`sire_id`),
  KEY `dam_id` (`dam_id`),
  KEY `straw_id` (`straw_id`),
  KEY `embryo_id` (`embryo_id`),
  KEY `country_id` (`country_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=27 ;


--
-- Table structure for table `cow_breed`
--

CREATE TABLE IF NOT EXISTS `cow_breed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cow_id` int(11) NOT NULL,
  `breed_id` int(11) NOT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cow_id` (`cow_id`),
  KEY `breed_id` (`breed_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=22 ;


--
-- Table structure for table `cow_deformity`
--

CREATE TABLE IF NOT EXISTS `cow_deformity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cow_id` int(11) NOT NULL,
  `deformity_id` int(11) NOT NULL,
  `date_added` datetime NOT NULL,
  `specification` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cow_id` (`cow_id`),
  KEY `deformity_id` (`deformity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `cow_event`
--

CREATE TABLE IF NOT EXISTS `cow_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cow_id` int(11) DEFAULT NULL,
  `event_id` int(11) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `event_date` date NOT NULL,
  `date_added` datetime NOT NULL,
  `birth_type` enum('Normal','Still') DEFAULT NULL,
  `parent_cow_event` int(11) DEFAULT NULL,
  `straw_id` int(11) DEFAULT NULL,
  `vet_id` int(11) DEFAULT NULL,
  `bull_id` int(11) DEFAULT NULL,
  `servicing_days` int(11) DEFAULT NULL,
  `cod_id` int(11) DEFAULT NULL COMMENT 'Cause of Death ID',
  PRIMARY KEY (`id`),
  KEY `cow_id` (`cow_id`,`event_id`),
  KEY `event_id` (`event_id`),
  KEY `parent_cow_event` (`parent_cow_event`),
  KEY `straw_id` (`straw_id`),
  KEY `vet_id` (`vet_id`),
  KEY `bull_id` (`bull_id`),
  KEY `cod_id` (`cod_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=20 ;


--
-- Table structure for table `deformity`
--

CREATE TABLE IF NOT EXISTS `deformity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `deformity`
--

INSERT INTO `deformity` (`id`, `name`) VALUES
(3, 'Abnormal teat number'),
(1, 'Blind'),
(5, 'Clef palate'),
(4, 'Crooked feet'),
(2, 'Lame'),
(6, 'Other');

-- --------------------------------------------------------

--
-- Table structure for table `embryo`
--

CREATE TABLE IF NOT EXISTS `embryo` (
  `id` int(11) NOT NULL,
  `embryo_no` varchar(100) NOT NULL,
  `dam_id` int(11) DEFAULT NULL,
  `sire_id` int(11) DEFAULT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `embryo_no` (`embryo_no`),
  KEY `dam_id` (`dam_id`,`sire_id`),
  KEY `sire_id` (`sire_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `event`
--

CREATE TABLE IF NOT EXISTS `event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=13 ;

--
-- Dumping data for table `event`
--

INSERT INTO `event` (`id`, `name`) VALUES
(1, 'Abortion'),
(9, 'Acquisition'),
(10, 'Artificial Insemination'),
(2, 'Birth'),
(4, 'Bloat'),
(11, 'Bull Servicing'),
(6, 'Death'),
(7, 'Dry Off'),
(8, 'Sale'),
(5, 'Sickness'),
(12, 'Signs of Heat'),
(3, 'Start of Lactation');

-- --------------------------------------------------------

--
-- Table structure for table `extension_personnel`
--

CREATE TABLE IF NOT EXISTS `extension_personnel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;


--
-- Table structure for table `farmer`
--

CREATE TABLE IF NOT EXISTS `farmer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `mobile_no` varchar(50) NOT NULL,
  `location_county` varchar(255) DEFAULT NULL,
  `location_district` varchar(255) DEFAULT NULL,
  `gps_longitude` varchar(15) DEFAULT NULL,
  `gps_latitude` varchar(15) DEFAULT NULL,
  `date_added` datetime NOT NULL,
  `extension_personnel_id` int(11) DEFAULT NULL,
  `sim_card_sn` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mobile_no` (`mobile_no`,`sim_card_sn`),
  KEY `extension_personnel_id` (`extension_personnel_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=17 ;


--
-- Table structure for table `milk_production`
--

CREATE TABLE IF NOT EXISTS `milk_production` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cow_id` int(11) NOT NULL,
  `time` enum('Morning','Afternoon','Evening','Combined') NOT NULL COMMENT '0 - morning, 1 - afternoot, 2 - evening, 3 - combined',
  `quantity` int(11) NOT NULL,
  `date_added` datetime NOT NULL,
  `date` date NOT NULL,
  `quantity_type` enum('Litres','KGs','Cups') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cow_id_2` (`cow_id`,`time`,`date`),
  KEY `cow_id` (`cow_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;


--
-- Table structure for table `straw`
--

CREATE TABLE IF NOT EXISTS `straw` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `straw_number` varchar(100) NOT NULL,
  `sire_id` int(11) DEFAULT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `straw_number` (`straw_number`),
  KEY `sire_id` (`sire_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;


--
-- Table structure for table `vet`
--

CREATE TABLE IF NOT EXISTS `vet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;


--
-- Constraints for dumped tables
--

--
-- Constraints for table `cow`
--
ALTER TABLE `cow`
  ADD CONSTRAINT `cow_ibfk_1` FOREIGN KEY (`farmer_id`) REFERENCES `farmer` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_ibfk_4` FOREIGN KEY (`straw_id`) REFERENCES `straw` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_ibfk_5` FOREIGN KEY (`embryo_id`) REFERENCES `embryo` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_ibfk_6` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_ibfk_7` FOREIGN KEY (`sire_id`) REFERENCES `cow` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_ibfk_8` FOREIGN KEY (`dam_id`) REFERENCES `cow` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `cow_breed`
--
ALTER TABLE `cow_breed`
  ADD CONSTRAINT `cow_breed_ibfk_1` FOREIGN KEY (`cow_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_breed_ibfk_2` FOREIGN KEY (`breed_id`) REFERENCES `breed` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `cow_deformity`
--
ALTER TABLE `cow_deformity`
  ADD CONSTRAINT `cow_deformity_ibfk_1` FOREIGN KEY (`cow_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_deformity_ibfk_2` FOREIGN KEY (`deformity_id`) REFERENCES `deformity` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `cow_event`
--
ALTER TABLE `cow_event`
  ADD CONSTRAINT `cow_event_ibfk_1` FOREIGN KEY (`cow_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_event_ibfk_2` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_event_ibfk_4` FOREIGN KEY (`straw_id`) REFERENCES `straw` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_event_ibfk_5` FOREIGN KEY (`vet_id`) REFERENCES `vet` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_event_ibfk_6` FOREIGN KEY (`bull_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_event_ibfk_7` FOREIGN KEY (`cod_id`) REFERENCES `cause_of_death` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_event_ibfk_8` FOREIGN KEY (`parent_cow_event`) REFERENCES `cow_event` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `embryo`
--
ALTER TABLE `embryo`
  ADD CONSTRAINT `embryo_ibfk_1` FOREIGN KEY (`dam_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `embryo_ibfk_2` FOREIGN KEY (`sire_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `farmer`
--
ALTER TABLE `farmer`
  ADD CONSTRAINT `farmer_ibfk_1` FOREIGN KEY (`extension_personnel_id`) REFERENCES `extension_personnel` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `milk_production`
--
ALTER TABLE `milk_production`
  ADD CONSTRAINT `milk_production_ibfk_1` FOREIGN KEY (`cow_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `straw`
--
ALTER TABLE `straw`
  ADD CONSTRAINT `straw_ibfk_1` FOREIGN KEY (`sire_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
