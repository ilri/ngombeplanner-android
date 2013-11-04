-- phpMyAdmin SQL Dump
-- version 3.5.8.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 04, 2013 at 07:43 PM
-- Server version: 5.5.34-0ubuntu0.13.04.1
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
  `age` int(11) DEFAULT '-1',
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
  UNIQUE KEY `farmer_id_2` (`farmer_id`,`ear_tag_number`),
  KEY `farmer_id` (`farmer_id`),
  KEY `sire_id` (`sire_id`),
  KEY `dam_id` (`dam_id`),
  KEY `straw_id` (`straw_id`),
  KEY `embryo_id` (`embryo_id`),
  KEY `country_id` (`country_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=45 ;

--
-- Dumping data for table `cow`
--

INSERT INTO `cow` (`id`, `farmer_id`, `name`, `ear_tag_number`, `date_of_birth`, `age`, `age_type`, `sex`, `sire_id`, `dam_id`, `straw_id`, `embryo_id`, `date_added`, `service_type`, `country_id`) VALUES
(22, 16, 'Rosy', 'nai-jas1', '0000-00-00 00:00:00', 2, 'Years', 'Female', NULL, NULL, NULL, NULL, '2013-09-16 12:01:26', 'Bull', 1),
(23, 16, 'Chris', 'nai-jas2', '0000-00-00 00:00:00', 5, 'Weeks', 'Male', NULL, 22, NULL, NULL, '2013-09-16 12:01:26', 'Bull', 1),
(24, 16, 'Betty', 'nai-jas3', '2013-03-16 00:00:00', 26, 'Weeks', 'Female', NULL, 23, NULL, NULL, '2013-09-16 14:33:26', 'AI', 60),
(25, 16, 'Calf3', 'nai-jas5', '2013-09-16 00:00:00', 0, 'Days', 'Female', NULL, 24, NULL, NULL, '2013-09-16 15:52:50', 'AI', 3),
(26, 16, 'Tesr', 'etd2', '2013-09-16 00:00:00', 258, 'Days', 'Female', NULL, 24, NULL, NULL, '2013-09-16 17:03:12', 'AI', 195),
(27, 20, 'T1', 'T1', '0000-00-00 00:00:00', -1, 'Days', 'Female', NULL, NULL, NULL, NULL, '2013-10-30 16:34:16', 'Bull', NULL),
(28, 20, 'T2', 'T2', '0000-00-00 00:00:00', -1, 'Days', 'Female', NULL, NULL, NULL, NULL, '2013-10-30 16:34:16', 'Bull', NULL),
(29, 23, 'T3', 'T3', '0000-00-00 00:00:00', 2, 'Years', 'Female', NULL, NULL, NULL, NULL, '2013-10-30 16:37:19', 'Bull', NULL),
(30, 23, 'T4', 'T4', '0000-00-00 00:00:00', 5, 'Weeks', 'Female', NULL, 29, NULL, NULL, '2013-10-30 16:37:20', 'Bull', NULL),
(31, 24, 'Rosy', 'Makena', '0000-00-00 00:00:00', 2, 'Years', 'Female', NULL, NULL, NULL, NULL, '2013-10-30 16:49:13', 'Bull', NULL),
(32, 16, 'Magama', 'Matm', NULL, -1, NULL, 'Male', NULL, NULL, NULL, NULL, '0000-00-00 00:00:00', NULL, NULL),
(33, 16, 'Magamd', 'Matn', NULL, -1, NULL, 'Male', NULL, NULL, NULL, NULL, '0000-00-00 00:00:00', NULL, NULL),
(34, 16, 'Maga', 'Mat', NULL, -1, NULL, 'Male', NULL, NULL, NULL, NULL, '0000-00-00 00:00:00', NULL, NULL),
(39, 16, '', 'Tamu', NULL, -1, NULL, 'Male', NULL, NULL, NULL, NULL, '0000-00-00 00:00:00', NULL, NULL),
(41, 16, 'J', 'Testim', NULL, -1, NULL, 'Male', NULL, NULL, NULL, NULL, '0000-00-00 00:00:00', NULL, NULL),
(42, 16, '', 'Da', NULL, -1, NULL, 'Male', NULL, NULL, NULL, NULL, '0000-00-00 00:00:00', NULL, NULL),
(43, 16, '', 'Daj', NULL, -1, NULL, 'Male', NULL, NULL, NULL, NULL, '0000-00-00 00:00:00', NULL, NULL),
(44, 16, '', 'Dajtgj', NULL, -1, NULL, 'Male', NULL, NULL, NULL, NULL, '0000-00-00 00:00:00', NULL, NULL);

-- --------------------------------------------------------

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=25 ;

--
-- Dumping data for table `cow_breed`
--

INSERT INTO `cow_breed` (`id`, `cow_id`, `breed_id`, `date_added`) VALUES
(14, 22, 6, '2013-09-16 12:01:26'),
(15, 22, 7, '2013-09-16 12:01:26'),
(16, 23, 6, '2013-09-16 12:01:26'),
(17, 23, 7, '2013-09-16 12:01:26'),
(18, 23, 9, '2013-09-16 12:01:26'),
(19, 26, 15, '2013-09-16 17:03:12'),
(20, 26, 17, '2013-09-16 17:03:12'),
(21, 26, 19, '2013-09-16 17:03:12'),
(22, 30, 5, '2013-10-30 16:37:20'),
(23, 31, 1, '2013-10-30 16:49:13'),
(24, 31, 4, '2013-10-30 16:49:13');

-- --------------------------------------------------------

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `cow_deformity`
--

INSERT INTO `cow_deformity` (`id`, `cow_id`, `deformity_id`, `date_added`, `specification`) VALUES
(1, 29, 6, '2013-10-30 16:37:19', 'Other'),
(2, 30, 1, '2013-10-30 16:37:20', NULL),
(3, 30, 3, '2013-10-30 16:37:20', NULL),
(4, 31, 1, '2013-10-30 16:49:13', NULL);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=41 ;

--
-- Dumping data for table `cow_event`
--

INSERT INTO `cow_event` (`id`, `cow_id`, `event_id`, `remarks`, `event_date`, `date_added`, `birth_type`, `parent_cow_event`, `straw_id`, `vet_id`, `bull_id`, `servicing_days`, `cod_id`) VALUES
(12, 22, 10, '', '2013-09-16', '2013-09-16 12:43:50', NULL, NULL, 1, 1, NULL, NULL, NULL),
(13, 22, 1, 'After drinking from a neighbours trough', '2013-09-16', '2013-09-16 14:29:50', NULL, 12, NULL, NULL, NULL, NULL, NULL),
(14, 24, 2, NULL, '2013-03-16', '2013-09-16 14:33:27', 'Normal', 12, NULL, NULL, NULL, NULL, NULL),
(15, 22, 1, '', '2013-09-16', '2013-09-16 14:55:35', NULL, 12, NULL, NULL, NULL, NULL, NULL),
(16, 25, 2, NULL, '2013-09-16', '2013-09-16 15:52:50', 'Normal', 12, NULL, NULL, NULL, NULL, NULL),
(17, 26, 2, NULL, '2013-09-16', '2013-09-16 17:03:13', 'Normal', 12, NULL, NULL, NULL, NULL, NULL),
(18, 22, 1, '', '2013-10-14', '2013-10-14 14:29:20', NULL, 12, NULL, NULL, NULL, NULL, NULL),
(19, 22, 12, 'Test', '2013-10-17', '2013-10-17 14:28:25', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(20, 24, 11, NULL, '2013-10-03', '2013-11-04 18:48:01', NULL, NULL, NULL, NULL, 32, NULL, NULL),
(24, 24, 10, NULL, '2013-10-03', '2013-11-04 19:02:16', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(25, 24, 10, NULL, '2013-10-03', '2013-11-04 19:06:11', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(26, 24, 10, NULL, '2013-10-03', '2013-11-04 19:06:19', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(27, 24, 10, NULL, '2013-10-03', '2013-11-04 19:06:26', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(28, 24, 10, NULL, '2013-10-03', '2013-11-04 19:08:11', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(29, 24, 10, NULL, '2013-10-03', '2013-11-04 19:11:10', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(30, 24, 10, NULL, '2013-10-03', '2013-11-04 19:12:29', NULL, NULL, 2, NULL, NULL, NULL, NULL),
(31, 24, 10, NULL, '2013-10-03', '2013-11-04 19:16:54', NULL, NULL, 2, NULL, NULL, NULL, NULL),
(32, 24, 10, NULL, '2013-10-03', '2013-11-04 19:17:20', NULL, NULL, 2, NULL, NULL, NULL, NULL),
(33, 24, 10, NULL, '2013-10-03', '2013-11-04 19:18:36', NULL, NULL, 2, NULL, NULL, NULL, NULL),
(34, 24, 10, NULL, '2013-10-03', '2013-11-04 19:19:29', NULL, NULL, 2, NULL, NULL, NULL, NULL),
(35, 24, 10, NULL, '2013-10-03', '2013-11-04 19:19:51', NULL, NULL, 2, NULL, NULL, NULL, NULL),
(36, 24, 10, NULL, '2013-10-03', '2013-11-04 19:20:24', NULL, NULL, 2, NULL, NULL, NULL, NULL),
(37, 24, 10, NULL, '2013-10-03', '2013-11-04 19:30:03', NULL, NULL, 2, NULL, NULL, NULL, NULL),
(38, 24, 10, NULL, '2013-10-03', '2013-11-04 19:31:29', NULL, NULL, 2, NULL, NULL, NULL, NULL),
(39, 24, 10, NULL, '2013-10-03', '2013-11-04 19:31:55', NULL, NULL, 2, NULL, NULL, NULL, NULL),
(40, 24, 10, NULL, '2013-10-03', '2013-11-04 19:32:27', NULL, NULL, 2, 2, NULL, NULL, NULL);

-- --------------------------------------------------------

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
-- Dumping data for table `embryo`
--

INSERT INTO `embryo` (`id`, `embryo_no`, `dam_id`, `sire_id`, `date_added`) VALUES
(0, 'embro1', NULL, NULL, '2013-09-15 21:01:03');

-- --------------------------------------------------------

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=15 ;

--
-- Dumping data for table `extension_personnel`
--

INSERT INTO `extension_personnel` (`id`, `name`, `date_added`) VALUES
(5, 'Tom Muriranji', '2013-09-15 18:51:53'),
(6, 'A', '2013-10-30 15:58:41'),
(7, 'Frank makau', '2013-10-30 16:05:33'),
(8, 'Jasom', '2013-10-30 16:21:46'),
(9, 'Rogue', '2013-10-30 16:34:16'),
(10, 'Ext', '2013-10-30 16:49:13'),
(11, 'Jason rogena', '2013-10-30 16:59:53'),
(12, 'Jas', '2013-10-30 17:02:10'),
(13, 'Ron', '2013-10-30 17:17:11'),
(14, 'Ij', '2013-10-30 17:24:46');

-- --------------------------------------------------------

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=30 ;

--
-- Dumping data for table `farmer`
--

INSERT INTO `farmer` (`id`, `name`, `mobile_no`, `location_county`, `location_district`, `gps_longitude`, `gps_latitude`, `date_added`, `extension_personnel_id`, `sim_card_sn`) VALUES
(16, 'Jason Rogena', '0722838686', NULL, NULL, '', '', '2013-09-16 12:01:26', 5, '89254028071000359956'),
(17, 'A', '22222', NULL, NULL, '', '', '2013-10-30 15:58:41', 6, ''),
(18, 'Jason gone', '0744112233', NULL, NULL, '', '', '2013-10-30 16:05:33', 7, ''),
(19, 'Test1', '0733669966', NULL, NULL, '', '', '2013-10-30 16:21:46', 8, ''),
(20, 'Test2', '0712558800', NULL, NULL, '', '', '2013-10-30 16:34:16', 9, ''),
(23, 'Testd', '0712558801', NULL, NULL, '', '', '2013-10-30 16:37:19', 9, ''),
(24, 'Jason rogena agwata', '0715023802', NULL, NULL, '', '', '2013-10-30 16:49:13', 10, ''),
(25, 'Emily rogena', '0721674647', NULL, NULL, '', '', '2013-10-30 16:59:53', 11, ''),
(26, 'Julius', '0725805247', NULL, NULL, '', '', '2013-10-30 17:01:14', 11, ''),
(27, 'Sam', '0741', NULL, NULL, '', '', '2013-10-30 17:02:10', 12, ''),
(28, 'Tot', '5805855', NULL, NULL, '', '', '2013-10-30 17:17:11', 13, ''),
(29, 'Tip', '0874558821', NULL, NULL, '', '', '2013-10-30 17:24:46', 14, '');

-- --------------------------------------------------------

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=11 ;

--
-- Dumping data for table `milk_production`
--

INSERT INTO `milk_production` (`id`, `cow_id`, `time`, `quantity`, `date_added`, `date`, `quantity_type`) VALUES
(3, 22, 'Morning', 23, '2013-10-04 13:11:35', '2013-10-03', 'KGs'),
(4, 22, 'Morning', 50, '2013-10-14 14:25:22', '2013-10-14', 'Litres'),
(5, 22, 'Combined', 2, '2013-10-23 16:43:48', '2013-10-23', 'Litres'),
(6, 24, 'Morning', 20, '2013-10-31 15:11:16', '2013-09-30', 'Litres'),
(8, 24, 'Afternoon', 20, '2013-10-31 15:12:35', '2013-09-30', 'Litres'),
(9, 24, 'Evening', 20, '2013-10-31 15:13:10', '2013-09-30', 'Litres'),
(10, 22, 'Morning', 10, '2013-10-31 15:50:33', '2013-09-30', 'Litres');

-- --------------------------------------------------------

--
-- Table structure for table `straw`
--

CREATE TABLE IF NOT EXISTS `straw` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `straw_number` varchar(100) DEFAULT NULL,
  `sire_id` int(11) DEFAULT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `straw_number` (`straw_number`),
  KEY `sire_id` (`sire_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `straw`
--

INSERT INTO `straw` (`id`, `straw_number`, `sire_id`, `date_added`) VALUES
(1, 'straw1', NULL, '2013-09-15 21:01:03'),
(2, 'Jap', 43, '2013-11-04 19:12:29');

-- --------------------------------------------------------

--
-- Table structure for table `vet`
--

CREATE TABLE IF NOT EXISTS `vet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `vet`
--

INSERT INTO `vet` (`id`, `name`, `date_added`) VALUES
(1, 'Frank Makau', '2013-09-16 00:57:11'),
(2, 'Jas', '2013-11-04 19:32:27');

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
