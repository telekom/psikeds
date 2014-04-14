KB-META:
id psiKedsLetterEnvelope
name "psiKeds letter envelope reference knowledge base"
release "0.1.1"
(C) "2014, Karsten Reincke, Deutsche Telekom AG, Darmstadt"

/* 
licensing 
 "proprietary license: All rights are reserved. Feel free to contact:
  opensource@telekom.de"
*/

licensing 
"CC-SA-BY 3.0. https://creativecommons.org/licenses/by-sa/3.0/
You should have got a version of this license together with this file."
created 2014-04-04
modified 2014-04-14 
engineer "Karsten Reincke"
"Marco Juliano"
description 
	"A reference knowledge base for testing 
  the psiKeds inference machine"
	"Domain: Convey a correctly packed and 
  stamped German C4/C5/C56/C6 letter"
	
PSIKEDS-SENSORS:
sensor NamedColor {
  label "Color-Names"
  description "perceptible colors denoted by their names"
}
str.att namedColorAzure [ NamedColor -> "azure" ]
str.att namedColorPink [ NamedColor -> "pink" ]
str.att namedColorIvory [ NamedColor -> "ivory" ]
str.att namedColorWhite [ NamedColor -> "white" ]

sensor StampValue {
	label "Stamp-Value"
	unit "Cent"
}
int.att Euro045 [ StampValue -> 45 ]
int.att Euro060 [ StampValue -> 60 ]
int.att Euro090 [ StampValue -> 90 ]
int.att Euro145 [ StampValue -> 145 ]
int.att Euro240 [ StampValue -> 240 ]

sensor PreprintedAddrField {
  label "Preprinted-Address-Field"
  description "contains a preprinted address field"
}
str.att withPreprintedAddrField [ PreprintedAddrField -> "T" ]
str.att withoutPreprintedAddrField [ PreprintedAddrField -> "F" ]

sensor PaperThickness {
  label "Paper-Thickness"
  description "thickness of paper as one criteria of paper quality"
  unit "g/qm"
}

int.att P70gQuality [ PaperThickness -> 70 ]
int.att P80gQuality [ PaperThickness -> 80 ]
int.att P90gQuality [ PaperThickness -> 90 ]

sensor DinANumber {
	label "Din-A-Numbers"
	description "sensor to perceive Din A Format Numbers"
}

int.att DinA0Number [ DinANumber -> 0 ]
int.att DinA1Number [ DinANumber -> 1 ]
int.att DinA2Number [ DinANumber -> 2 ]
int.att DinA3Number [ DinANumber -> 3 ]
int.att DinA4Number [ DinANumber -> 4 ]
int.att DinA5Number [ DinANumber -> 5 ]
int.att DinA6Number [ DinANumber -> 6 ]
int.att DinA7Number [ DinANumber -> 7 ]
int.att DinA8Number [ DinANumber -> 8 ]
int.att DinA9Number [ DinANumber -> 9 ]
int.att DinA10Number [ DinANumber -> 10 ]

sensor DinCNumber {
	label "Din-C-Numbers"
	description "sensor to perceive Din C Format Numbers"
}

float.att DinC0Number [ DinCNumber -> 0.0 ]
float.att DinC1Number [ DinCNumber -> 1.0 ]
float.att DinC2Number [ DinCNumber -> 2.0 ]
float.att DinC3Number [ DinCNumber -> 3.0 ]
float.att DinC4Number [ DinCNumber -> 4.0 ]
float.att DinC5Number [ DinCNumber -> 5.0 ]
float.att DinC56Number [ DinCNumber -> 5.6 ]
float.att DinC6Number [ DinCNumber -> 6.0 ]
float.att DinC7Number [ DinCNumber -> 7.0 ]
float.att DinC8Number [ DinCNumber -> 8.0 ]
float.att DinC9Number [ DinCNumber -> 9.0 ]
float.att DinC10Number [ DinCNumber -> 10.0 ]

sensor DinFormatHeight {
  label "Din-Format-Height"
  description 
  	"DIN form based heights of paper sizes:
	 DIN-A as portrait (height > width )
     DIN-C as landscape (width > height)"
  unit "cm"
  }
float.att DinA0Height [ DinFormatHeight -> 118.9 ]
float.att DinA1Height [ DinFormatHeight -> 84.1 ]
float.att DinA2Height [ DinFormatHeight -> 59.4 ]
float.att DinA3Height [ DinFormatHeight -> 42.0 ]
float.att DinA4Height [ DinFormatHeight -> 29.7 ]
float.att DinA5Height [ DinFormatHeight -> 21.0 ]
float.att DinA6Height [ DinFormatHeight -> 14.8 ]
float.att DinA7Height [ DinFormatHeight -> 10.5 ]
float.att DinA8Height [ DinFormatHeight -> 7.4 ]
float.att DinA9Height [ DinFormatHeight -> 5.2 ]
float.att DinA10Height [ DinFormatHeight -> 3.7 ]

float.att DinC0Height [ DinFormatHeight -> 91.7 ]
float.att DinC1Height [ DinFormatHeight -> 64.8 ]
float.att DinC2Height [ DinFormatHeight -> 45.8 ]
float.att DinC3Height [ DinFormatHeight -> 32.4 ]
float.att DinC4Height [ DinFormatHeight -> 22.9 ]
float.att DinC5Height [ DinFormatHeight -> 16.2 ]
float.att DinC6Height [ DinFormatHeight -> 11.4 ]
float.att DinC7Height [ DinFormatHeight -> 8.1 ]
float.att DinC8Height [ DinFormatHeight -> 5.7 ]
float.att DinC9Height [ DinFormatHeight -> 4.0 ]
float.att DinC10Height [ DinFormatHeight -> 2.8 ]

sensor DinFormatWidth {
  label "Din-Format-Width"
  description 
  	"DIN form based widths of paper sizes:
	 DIN-A as portrait (height > width )
     DIN-C as landscape (width > height)"
  unit "cm"
  }
float.att DinA0Width [ DinFormatWidth -> 84.1 ]
float.att DinA1Width [ DinFormatWidth -> 59.4 ]
float.att DinA2Width [ DinFormatWidth -> 42.0 ]
float.att DinA3Width [ DinFormatWidth -> 29.7 ]
float.att DinA4Width [ DinFormatWidth -> 21.0 ]
float.att DinA5Width [ DinFormatWidth -> 14.8 ]
float.att DinA6Width [ DinFormatWidth -> 10.5 ]
float.att DinA7Width [ DinFormatWidth -> 7.4 ]
float.att DinA8Width [ DinFormatWidth -> 5.2 ]
float.att DinA9Width [ DinFormatWidth -> 3.7 ]
float.att DinA10Width [ DinFormatWidth -> 2.6 ]

float.att DinC0Width [ DinFormatWidth -> 129.7 ]
float.att DinC1Width [ DinFormatWidth -> 91.7 ]
float.att DinC2Width [ DinFormatWidth -> 64.8 ]
float.att DinC3Width [ DinFormatWidth -> 45.8 ]
float.att DinC4Width [ DinFormatWidth -> 32.4 ]
float.att DinC5Width [ DinFormatWidth -> 22.9 ]
float.att DinC6Width [ DinFormatWidth -> 16.2 ]
float.att DinC7Width [ DinFormatWidth -> 11.4 ]
float.att DinC8Width [ DinFormatWidth -> 8.1 ]
float.att DinC9Width [ DinFormatWidth -> 5.7 ]
float.att DinC10Width [ DinFormatWidth -> 4.0 ]

sensor Weight {
  label "weight"
  description "weight of an envelope or a paper page or both together"
  unit "g"
}
float.range.att envelopeWeight [Weight >> ( min 2.9 , max 20.0 , inc 1.1)]
float.range.att paperWeight [Weight >> ( min 1.1 , max 27.5 , inc 1.1)]
float.range.att letterWeight [Weight >> ( min 4.0 , max 47.5 , inc 1.1)]

sensor CountSectors {
  label "count-sectors"
  description "counts the number of sections evoked by the folding method"
}
int.range.att howManySections [ CountSectors >> ( min 1 , max 4 , inc 1)]

PSIKEDS-CONCEPTS:

concept DinA6Letter {
  label "DIN-A6-Values"
  description "DIN A6 constitutive values"
  [
  	*int> DinA6Number
  	and *float> DinA6Height
  	and *float> DinA6Width
  ]
}

concept DinA5Letter {
  label "DIN-A5-Values"
  description "DIN A5 constitutive values"
  [
  	*int> DinA5Number
  	and *float> DinA5Height
  	and *float> DinA5Width
  ]
}

concept DinA4Letter {
  label "DIN-A4-Values"
  description "DIN A4 constitutive values"
  [
  	*int> DinA4Number
  	and *float> DinA4Height
  	and *float> DinA4Width
  ]
}

concept DinA3Letter {
  label "DIN-A3-Values"
  description "DIN A3 constitutive values"
  [
  	*int> DinA3Number
  	and *float> DinA3Height
  	and *float> DinA3Width
  ]
}

concept DinA2Letter {
  label "DIN-A2-Values"
  description "DIN A2 constitutive values"
  [
  	*int> DinA2Number
  	and *float> DinA2Height
  	and *float> DinA2Width
  ]
}

concept DinA6CardConveyable {
  label "Conveyable-DIN-A6-Writing-Card"
  description "constitutive values for a conveyable DIN A6 writing card"
  [
  	*str> withPreprintedAddrField
  	and *int> DinA6Number
  	and *float> DinA6Height
  	and *float> DinA6Width
  ]
}

concept DinA6CardUnconveyable {
  label "Conveyable-DIN-A6-Writing-Card"
  description "constitutive values for an unconveyable DIN A6 writing card"
  [
  	*str> withPreprintedAddrField
  	and *int> DinA6Number
  	and *float> DinA6Height
  	and *float> DinA6Width
  ]
}

concept DinC4Envelope {
  label "DIN-C4-Envelope"
  description "DIN C4 constitutive values"
  [
  	*float> DinC4Number
  	and *float> DinC4Height
  	and *float> DinC4Width
  ]
}

concept DinC5Envelope {
  label "DIN-C5-Envelope"
  description "DIN C5 constitutive values"
  [
  	*float> DinC5Number
  	and *float> DinC5Height
  	and *float> DinC5Width
  ]
}

concept DinC56Envelope {
  label "DIN-C5,6-Envelope"
  description "DIN C5,6 constitutive values"
  [
  	*float> DinC56Number
  	and *float> DinC6Height
  	and *float> DinC5Width
  ]
}

concept DinC6Envelope {
  label "DIN-C6-Envelope"
  description "DIN C6 constitutive values"
  [
  	*float> DinC6Number
  	and *float> DinC6Height
  	and *float> DinC6Width
  ]
}

PSIKEDS-PURPOSES:
purpose informRemoteAddressee {
	root true
 	label "inform-remote-addressee"
	description "something to inform a remote addressees"
}
	
purpose fileMessages {
	root false
	label "write-messages-on"
	description "something to write a messages on it"
}

purpose compressMessageMedia {
	label "compress-message-media"
	description "something usable to compress the media containing the messages"
}

purpose protectMessage {
	label "protect-message-media"
	description "something usable to protect the media containing the messages"
}
	
purpose enableMessageTransport {
	label "enable-message-transport"
	description "something usable to enable the transport of a protected message"
}

PSIKEDS-VARIANTS:
variant CompressedLetter {
	label "Compressed-Letter"
	description "a letter containing a compressed media"
	specified.by NamedColor as [
		*str> namedColorAzure
		or *str> namedColorPink
		or *str> namedColorIvory
		or *str> namedColorWhite
  ] 
  specified.by DinCNumber as [
		*float> DinC4Number 
		or *float> DinC5Number
		or *float> DinC56Number
		or *float> DinC6Number    	
  ]
  perceived.by Weight within *floatRange> letterWeight
}


variant UncompressedLetter {
	label "Uncompressed-Letter"
	description "a letter containing an uncompressed media"
	specified.by NamedColor as [
		*str> namedColorAzure
		or *str> namedColorPink
		or *str> namedColorIvory
		or *str> namedColorWhite
  ]
  specified.by DinCNumber as [
	 *float> DinC4Number
		or *float> DinC5Number
		or *float> DinC6Number    	
  ]
  perceived.by Weight within *floatRange> letterWeight
}

variant Postcard {
	label "Postcard"
	description "standard postcard"
	specified.by NamedColor as [
		*str> namedColorIvory
		or *str> namedColorWhite
  ]
  perceived.by Weight within *floatRange> letterWeight
}

variant Envelope {
	label "Envelope"
	description "an envelope for protecting a written message"
	specified.by NamedColor as [
		*str> namedColorAzure
		or *str> namedColorPink
		or *str> namedColorIvory
		or *str> namedColorWhite
  ]
  perceived.by Weight within *floatRange> envelopeWeight
  classified.as [ 
  	*concept> DinC4Envelope
  	or *concept> DinC5Envelope
   	or *concept> DinC56Envelope
  	or *concept> DinC6Envelope
  ]
}

variant WritingPaper {
	label "WritingPaper"
	description "a page of letter paper"
	specified.by NamedColor as [
		*str> namedColorAzure
		or *str> namedColorPink
		or *str> namedColorIvory
		or *str> namedColorWhite
    ]
  specified.by PaperThickness as [
  	*int> P70gQuality
  	or *int> P80gQuality
  	or *int> P90gQuality
  ]
  perceived.by Weight within *floatRange> paperWeight
  perceived.by CountSectors within *intRange> howManySections
  classified.as [
  	*concept> DinA6Letter
   	or *concept> DinA5Letter
   	or *concept> DinA4Letter
   	or *concept> DinA3Letter
   	or *concept> DinA2Letter
  ]
}

variant WritingCard {
	label "WritingCard"
	description "a card for writing a message"
	specified.by NamedColor as [
		*str> namedColorAzure
		or *str> namedColorPink
		or *str> namedColorIvory
		or *str> namedColorWhite
    ]
  classified.as [
    *concept> DinA6CardConveyable
    or *concept> DinA6CardUnconveyable
  ]
}

variant FoldMethod12 {
	label "Fold-Method-Half-in-Size"
	description "a method to fold a piece of paper half in size"
}

variant FoldMethod13 {
	label "Fold-Method-a-third-in-Size"
	description "a method to fold a piece of paper a third in size"
}

variant FoldMethod14 {
	label "Fold-Method-a-quarter-in-Size"
	description "a method to fold a piece of paper a quarter in size"
}


variant Stamp {
	label "Stamp"
	description "a stamp indicating that the transport is paid"
	specified.by StampValue as [
		*int> Euro045
		or *int> Euro060
		or *int> Euro090
		or *int> Euro145
		or *int> Euro240
	]
}

PSIKEDS-IS-FULFILLED-BY-STATEMENTS:
purpose.system informRemoteAddressee isFulfilledBy {
	CompressedLetter UncompressedLetter Postcard
}

purpose.system compressMessageMedia isFulfilledBy {
	FoldMethod12 FoldMethod13 FoldMethod14
}

purpose.system protectMessage isFulfilledBy { Envelope }

purpose.system fileMessages isFulfilledBy { WritingPaper WritingCard }

purpose.system enableMessageTransport isFulfilledBy { Stamp }

PSIKEDS-IS-CONSTITUTED-BY-STATEMENTS:
purpose.variant CompressedLetter isConstitutedBy {
  < 1 instance(s)-of compressMessageMedia >
	< 1 instance(s)-of  protectMessage >
	< 1 instance(s)-of  fileMessages >
	< 1 instance(s)-of  enableMessageTransport >
}

purpose.variant UncompressedLetter isConstitutedBy { 
	< 1 instance(s)-of protectMessage >
	< 1 instance(s)-of  fileMessages >
	< 1 instance(s)-of  enableMessageTransport >
}

purpose.variant Postcard isConstitutedBy { 
  < 1 instance(s)-of fileMessages >
  < 1 instance(s)-of enableMessageTransport >
}

PSIKEDS-LOGICAL-INFERENCE-ELEMENTS:

event.var writingCardAsPostcard [
	label "the-writing-card-as-a-postcard"
	description "using the writing card for filing the message of a postcard"
	context < Postcard fileMessages >
	fact *variant> WritingCard
]

event.str preparedAddressAreaOnWritingCardAsPostcard [
	label "preprinted-address-area-on-writing-card-as-postcard"
	description "writing card with preprinted address area being used as postcard"
	context < Postcard fileMessages WritingCard >
	fact *str> withPreprintedAddrField
]

event.int stamp045AsPostcardEnabler [
	label "045-cent-stamp-as-postcard-transport-enabler"
	description "45 cent stamp for enabling the transport of a postcard"
	context < Postcard fileMessages WritingCard >
	fact *int> Euro045 
]

event.var uncompressedLetterWithCardAsFileMedia [
  label "the-writing-card-used-as-uncompressed-letter-media"
  description "using the writing card for filing the message of an uncompressed letter"
  context < UncompressedLetter fileMessages >
  fact *variant> WritingCard
]

event.str uncompressedLetterWritingCardWithoutAddressField [
	label "an-uncompressed-letter-with-a-writing-card-without-addr-field"
    description "no preprinted address area on a writing card used as uncompressed letter"
    context < UncompressedLetter fileMessages WritingCard >
    NOT
    fact *str> withPreprintedAddrField
]

event.concept uncompressedLetterInDinC6Envelope [
	label "uncompressed-letter-in-a-DIN-C6-envelope"
	context < UncompressedLetter protectMessage Envelope >
	fact *concept> DinC6Envelope
]


event.float UncompressedDinC6Letter [
	label "uncompressed-Din-C6-Letter"
	description "a typical DIN C6 Letter denoted by its Din-Number"
	context < UncompressedLetter >
	fact *float> DinC6Number
]

/* Note: Letter size 5.6 for uncompressed letter not defined */
event.concept UncompressedDinC5Letter [
	label "uncompressed-Din-C5-Letter"
	description "a typical DIN C5 Letter denoted by the concept of all DIN-C6-features of its envelope"
	context < UncompressedLetter  protectMessage Envelope>
	fact *concept> DinC5Envelope
]

event.float UncompressedDinC4Letter [
	label "uncompressed-Din-C4-Letter"
	description "a typical DIN C4 Letter denoted by the Din-Number of the envelope"
	context < UncompressedLetter protectMessage Envelope>
	fact *float> DinC4Number
]

event.int UncompressedLetterStamp060Cent [
	label "uncompressed-letter-stamp-with-value-60"
	context < UncompressedLetter enableMessageTransport Stamp >
	fact *int> Euro060
]

event.int UncompressedLetterStamp145Cent [
	label "uncompressed-letter-stamp-with-value-60"
	context < UncompressedLetter enableMessageTransport Stamp >
	fact *int> Euro145
]


event.var compressedLetterWithPaper [
	label "the-compressed-letter-written-on-paper"
	context < CompressedLetter fileMessages >
	fact *variant> WritingPaper
]



event.int compressedLetterWithDinA2Paper [
	label "compressed-letter-with-DIN-A2-paper"
	description "DIN-A2 paper in the context of a compressed letter"
	context < CompressedLetter fileMessages WritingPaper >
	fact *int> DinA2Number
]

event.int compressedLetterWithDinA3Paper [
	label "compressed-letter-with-DIN-A3-paper"
	description "DIN-A3 paper in the context of a compressed letter"
	context < CompressedLetter fileMessages WritingPaper >
	fact *int> DinA3Number
]

event.int compressedLetterWithDinA4Paper [
	label "compressed-letter-with-DIN-A4-paper"
	description "DIN-A4 paper in the context of a compressed letter"
	context < CompressedLetter fileMessages WritingPaper >
	fact *int> DinA4Number
]

event.int compressedLetterWithDinA5Paper [
	label "compressed-letter-with-DIN-A5-paper"
	description "DIN-A5 paper in the context of a compressed letter"
	context < CompressedLetter fileMessages WritingPaper >
	fact *int> DinA5Number
]

event.float compressedLetterWithDinC4Envelope [
	label "compressed-letter-with-DIN-C4-envelope"
	description "DIN-C4 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope  >
	fact *float> DinC4Number
]

event.float compressedLetterWithDinC5Envelope [
	label "compressed-letter-with-DIN-C5-envelope"
	description "DIN-C5 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope >
	fact *float> DinC5Number
]

event.float compressedLetterWithDinC56Envelope [
	label "compressed-letter-with-DIN-C56-envelope"
	description "DIN-C56 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope >
	fact *float> DinC56Number
]

event.float compressedLetterWithoutDinC56Envelope [
	label "compressed-letter-without-DIN-C56-envelope"
	description "not using a DIN-C56 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope >
	NOT
	fact *float> DinC56Number
]

event.float compressedLetterWithDinC6Envelope [
	label "compressed-letter-with-DIN-C6-envelope"
	description "DIN-C6 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope >
	fact *float> DinC6Number
]

event.float compressedLetterWithoutDinC6Envelope [
	label "compressed-letter-without-DIN-C6-envelope"
	description "not using a DIN-C6 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope >
	NOT
	fact *float> DinC6Number
]
 event.var compressedLetterFoldHalfInSize [
	label "compressed-letter-using-the-half-in-size-folding-method"
	context < CompressedLetter compressMessageMedia >
	fact *variant> FoldMethod12
]

 event.var compressedLetterFoldThirdInSize [
	label "compressed-letter-using-the-third-in-size-folding-method"
	context < CompressedLetter compressMessageMedia >
	fact *variant> FoldMethod13
]

 event.var compressedLetterFoldQuarterInSize [
	label "compressed-letter-using-the-quarter-in-size-folding-method"
	context < CompressedLetter compressMessageMedia >
	fact *variant> FoldMethod14
]


event.float compressedDinC6Letter [
	label "compressed-Din-C6-Letter"
	description "a typical compressed DIN C6 Letter denoted by its Din-Number"
	context < CompressedLetter >
	fact *float> DinC6Number
]

event.float compressedDinC56Letter [
	label "compressed-Din-C56-Letter"
	description "a typical compressed DIN C56 Letter denoted by its Din-Number"
	context < CompressedLetter >
	fact *float> DinC56Number
]

event.float compressedDinC5Letter [
	label "compressed-Din-C5-Letter"
	description "a typical compressed DIN C5 Letter denoted by its Din-Number"
	context < CompressedLetter >
	fact *float> DinC5Number
]

event.float compressedDinC4Letter [
	label "compressed-Din-C4-Letter"
	description "a typical compressed DIN C4 Letter denoted by its Din-Number"
	context < CompressedLetter >
	fact *float> DinC4Number
]

event.int compressedLetterStamp060Cent [
	label "uncompressed-letter-stamp-with-value-60"
	context < CompressedLetter enableMessageTransport Stamp >
	fact *int> Euro060
]

event.int compressedLetterStamp145Cent [
	label "uncompressed-letter-stamp-with-value-145"
	context < CompressedLetter enableMessageTransport Stamp >
	fact *int> Euro145
]

event.enforcing PostcardAsContextTop [
	label "postcard-itself-as-top-context"
	context=fact *variant> Postcard
]

event.enforcing UncompressedLetterAsContextTop [
	label "uncompressed-letter-itself-as-top-context"
	context=fact *variant> UncompressedLetter
]

event.enforcing CompressedLetterAsContextTop [
	label "compressed-letter-itself-as-top-context"
	context=fact *variant> CompressedLetter
]

PSIKEDS-RELATIONAL-INFERENCE-ELEMENTS:

relation.param PostcardColor [
  label "Postcard-Color"
  context < Postcard >
  value.type *sensor> NamedColor
]

relation.param WritingCardPostcardColor [
  label "Writing-Card-Color-Used-As-Postcard"
  context < Postcard fileMessages WritingCard >
  value.type *sensor> NamedColor
]

relation.param uncompressedLetterEnvelopDinSize [
  label "uncompressed-letter-envelope-din-size"
  context < UncompressedLetter protectMessage Envelope >
  value.type *sensor> DinCNumber
]

relation.param uncompressedLetterEnvelopColor [
  label "uncompressed-letter-envelope-color"
  context < UncompressedLetter protectMessage Envelope >
  value.type *sensor> NamedColor
]


relation.param uncompressedLetterPaperDinSize [
  label "uncompressed-letter-paper-din-size"
  context < UncompressedLetter fileMessages WritingPaper >
  value.type *sensor> DinANumber
]


relation.param uncompressedLetterDinSize [
  label "uncompressed-letter-din-size"
  description "the size of an uncompressed letter itself"
  context < UncompressedLetter >  
  value.type *sensor> DinCNumber
]

relation.param uncompressedLetterColor [
  label "uncompressed-letter-color"
  description "the color of an uncompressed letter itself"
  context < UncompressedLetter >
  value.type *sensor> NamedColor
]

relation.param compressedLetterEnvelopeColor [
  label "compressed-letter-envelope-color"
  description "the color of the envelope of a compressed"
  context < CompressedLetter protectMessage Envelope>
  value.type *sensor> NamedColor
]

relation.param compressedLetterDinSize [
  label "compressed-letter-din-size"
  description "the size of the compressed letter itself"
  context < CompressedLetter >
  value.type *sensor> DinCNumber
]

relation.param compressedLetterColor [
  label "compressed-letter-color"
  description "the color of the compressed letter itself"
  context < CompressedLetter >
  value.type *sensor> NamedColor
]


relation.param compressedLetterPaperSize [
  label "compressed-letter-paper-size"
  description "size of the paper in the context of a compressed letter"
  context < CompressedLetter fileMessages WritingPaper >
  value.type *sensor> DinANumber
]

relation.param compressedLetterEnvelopeSize [
  label "compressed-letter-envelope-size"
  description "size of the envelope in the context of a compressed letter"
  context < CompressedLetter protectMessage Envelope >
  value.type *sensor> DinCNumber
]

PSIKEDS-INFERENCE-CONDITIONS:

/* RULE-1.A */
logic.enforcer IFpostcardTHENuseWritingCard
label "postcard=>use-writing-card"
description "Rule 1.A: a postcard must be written on a writing card"
means (PostcardAsContextTop -> writingCardAsPostcard)

/* RULE-1.B */
logic.enforcer IFpostcardTHENuseWritingCardWithAddressField
label "postcard=>use-writing-card-with-preprinted-address-field"
description "Rule 1.B: a postcard must be written on 
  a writing card with preprinted address field"
means (PostcardAsContextTop -> preparedAddressAreaOnWritingCardAsPostcard)

/* RULE-1.C */
logic.enforcer IFpostcardTHENuse45CentStamp
label "postcard=>45-cent-stamp"
description "Rule 1.C: a postcard costs 45 cents"
means (PostcardAsContextTop -> stamp045AsPostcardEnabler)

/* RULE-1.D */
relation ColorOfPostCardIsColorOfWritingCard
label "color-of-the-postcard-is-the-color-of-the-writing-card"
description "Rule 1.D"
means ( PostcardColor equal WritingCardPostcardColor )

/* RULE-2.A */
logic.enforcer IFuncompLetterTHENuseCardWithoutAddressField
label "uncompressed-letter-on-a-card=>card-without-preprinted-address-field"
description "Rule 2.A"
means ( UncompressedLetterAsContextTop 
  -> uncompressedLetterWritingCardWithoutAddressField)

/* RULE-2.B */
logic.rule IfuncompLetterWithCardTHENuseC6Envelope
label "uncompressed-letter-on-a-card=>uncompressedLetterInDinC6Envelope" 
description "Rule-2.B"
means ({ uncompressedLetterWithCardAsFileMedia } -> uncompressedLetterInDinC6Envelope)

/* RULE-2.C */
relation EnvelopeDinSizeOfUncomLettIsLessOrEqualPaperDinSize
label "the-size-of-an-uncompressed-letter-envelope-is-less-or-equal-its-paper-din-size"
description "Rule 2.C"
means ( uncompressedLetterEnvelopDinSize lessOrEqual uncompressedLetterPaperDinSize)

/* RULE-2.D */
relation SizeOfUncomLettIsSizeOfEnevlope
label "the-size-of-an-uncompressed-letter-is-the-size-of-its-envelope"
description "Rule 2.D"
means ( uncompressedLetterDinSize equal uncompressedLetterEnvelopDinSize)

/* RULE-2.E */
relation ColorOfUncompLetterIsColorOfEnvelope
label "color-of-an-uncompressed-letter-is-color-of-its-envelope"
description "Rule 2.G"
means ( uncompressedLetterColor equal uncompressedLetterEnvelopColor)

/* RULE-2.F.a */
logic.rule IFuncompDinC6LetterTHENuse60CentStamp
label "uncompressed-DINC6-letter=>60-cent-stamp"
description "Rule 2.F.a"
means ({ UncompressedDinC6Letter } -> UncompressedLetterStamp060Cent)

/* RULE-2.F.b */
logic.rule IFuncompDinC5LetterTHENuse145CentStamp
label "uncompressed-DINC5-letter=>145-cent-stamp"
description "Rule 2.F.b"
means ({ UncompressedDinC5Letter } -> UncompressedLetterStamp145Cent)

/* RULE-2.F.c */
logic.rule IFuncompDinC4LetterTHENuse145CentStamp
label "uncompressed-DINC4-letter=>145-cent-stamp"
description "Rule 2.F.c"
means ({UncompressedDinC4Letter} -> UncompressedLetterStamp145Cent)

/* RULE-3.A */
logic.enforcer IFcompressedLetterTHENusePaper
label "compressed-letter=>use-paper"
description "Rule 3.A: foldable letters must be written on paper"
means (CompressedLetterAsContextTop -> compressedLetterWithPaper)

/* RULE 3.B */  
relation EnvelopeDinSizeOfCompLettIsLessThanPaperDinSize
label "the-size-of-an-compressed-letter-envelope-is-less-than-its-paper-din-size"
description "Rule 3.B"
means ( compressedLetterEnvelopeSize less compressedLetterPaperSize)

/* A2 paper only in C4 envelope by using the method 'quarter in size' */
/* RULE-3.C.a */
logic.rule IFcompLetterWithDinA2PaperTHENfoldQuarterInSize
label "Convey-Din-A2-Paper-only-folded-a-quarter-in-size"
description "Rule 3.C.A" 
means ({compressedLetterWithDinA2Paper } -> compressedLetterFoldQuarterInSize)

/* RULE-3.C.b */
logic.rule IFcompLetterWithDinA2PaperThenOnlyDINC4Envelope
label "Convey-Din-A2-Paper-only-in-Din-C4-Envelope"
description "Rule 3.C.C"
means ({ compressedLetterWithDinA2Paper } -> compressedLetterWithDinC4Envelope)

/* RULE-3.D.a */
logic.rule IFA3PaperInC4EnvelopenTHENhalfInSize
label "A3-Paper-in-C4-envelope-must-be-folded-half-in-size"
description "Rule-3.D.a"
means ({ compressedLetterWithDinA3Paper 
     and compressedLetterWithDinC4Envelope
      } -> compressedLetterFoldHalfInSize)
      
/* RULE-3.D.b */ 
logic.rule IFA3PaperInC5EnvelopenTHENquarterInSize
label "A3-paper-in-C5-envelope-must-be-folded-quarter-in-size"
description "Rule-3.D.b"
means ({ compressedLetterWithDinA3Paper 
     and compressedLetterWithDinC5Envelope 
       } -> compressedLetterFoldQuarterInSize)
       
/* A3 paper only in C4 or C5 envelope, hence not in C6 or C56 ... */
/* RULE-3.D.c */
logic.rule IFcompLetterWithDinA3PaperThenNotDinC56Envelope
label "Dont-Convey-Din-A3-Paper-in-Din-C56-Envelope"
description "Rule 3.D.c"
means ({ compressedLetterWithDinA3Paper } -> compressedLetterWithoutDinC56Envelope)

/* RULE-3.D.d */
logic.rule IFcompLetterWithDinA3PaperThenNotDinC6Envelope
label "Dont-Convey-Din-A3-Paper-in-Din-C6-Envelope"
description "Rule 3.D.d"
means ({ compressedLetterWithDinA3Paper } -> compressedLetterWithoutDinC6Envelope)



/* A4 paper only in C5, C56, C6 envelope by ... */
/* RULE-3.E.a */ 

logic.rule IFA4PaperInC5EnvelopenTHENhalfInSize
label "A4-Paper-in-C5-envelope-must-be-folded-half-in-size"
description "Rule-3.E.a"
means ({ compressedLetterWithDinA4Paper 
     and compressedLetterWithDinC5Envelope 
       } -> compressedLetterFoldHalfInSize)
       
/* RULE-3.E.b */ 
logic.rule IFA4PaperInC56EnvelopenTHENthirdInSize
label "A4-Paper-in-C56-envelope-must-be-folded-third-in-size"
description "Rule-3.E.b"
means ({ compressedLetterWithDinA4Paper 
     and compressedLetterWithDinC56Envelope 
       } -> compressedLetterFoldThirdInSize)
         
/* RULE-3.E.c */
logic.rule IFA4PaperInC6EnvelopenTHENquarterInSize
label "A4-Paper-in-C6-envelope-must-be-folded-quarter-in-size"
description "Rule-3.E.c"
means ({ compressedLetterWithDinA4Paper 
     and compressedLetterWithDinC6Envelope 
       } -> compressedLetterFoldQuarterInSize)
       
/* RULE-3.F.a */  
logic.rule IFfoldingThirdInSizeTHENdinC56Envelope
label "folding-third-in-size-requires-a-C56-envelope"
description "Rule 3.F.a" 
means ({compressedLetterFoldThirdInSize} -> compressedLetterWithDinC56Envelope)

/* RULE-3.F.b */  
logic.rule IFfoldingThirdInSizeTHENa4Paper
label "folding-third-in-size-requires-a-A4-paper"
description "Rule 3.F.b" 
means ({ compressedLetterFoldThirdInSize } -> compressedLetterWithDinA4Paper)

/* A5 paper only in C6 envelope by ... */ 
/* RULE-3.G */ 
logic.rule IfcompLetterWithA5PaperTHENfoldHalfInSize
label "A5-Paper-in-C6-envelope-must-be-folded-half-in-size"
description "Rule-3.G"
means ({ compressedLetterWithDinA5Paper } -> compressedLetterFoldHalfInSize )

/* RULE-3.H.a */
relation SizeOfComLettIsSizeOfEnevlope
label "size-of-a-compressed-letter-is-the-size-of-its-envelope"
description "Rule 3.H.a"
means (compressedLetterEnvelopeSize equal compressedLetterDinSize)

/* RULE-3.H.b */
relation ColorOfCompLetterIsColorOfEnvelope
label "color-of-a-compressed-letter-is-the-color-of-its-envelope"
description "Rule 3.H.b"
means ( compressedLetterEnvelopeColor equal compressedLetterColor)

/* RULE-3.I.a */
logic.rule IFcompC6LetterTHEN60CentStamp
label "compressed-C6-letter=>60-cent-stamp"
description "Rule 3.I.a"
means ({ compressedDinC6Letter } ->   compressedLetterStamp060Cent)

/* RULE-3.I.b */ 
logic.rule IFcompC56LetterTHEN60CentStamp
label "compressed-C56-letter=>60-cent-stamp"
description "Rule 3.I.b"
means ({compressedDinC56Letter } -> compressedLetterStamp060Cent)

/* RULE-3.I.c */ 
logic.rule IFcompC5LetterTHEN145CentStamp
label "compressed-C5-letter=>145-cent-stamp"
description "Rule 3.I.c" 
means ({compressedDinC5Letter } -> compressedLetterStamp145Cent )

/* RULE-3.I.d */ 
logic.rule IFcompC4LetterTHEN145CentStamp
label "compressed-C4-letter=>145-cent-stamp"
description "Rule 3.I.d" 
means ({compressedDinC4Letter } -> compressedLetterStamp145Cent)






