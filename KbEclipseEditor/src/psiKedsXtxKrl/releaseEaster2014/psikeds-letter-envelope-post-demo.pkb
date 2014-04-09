name "post envelope aggregation"
release "0.1.1"
(C) "2015, Karsten Reincke, Frankfurt"
created 2014-04-04
modified 2014-04-05 
engineer "Karsten Reincke"
"Marco Juliano"
description 
	"Description"
	"Beschreibung"
	
sensor NamedColor {
  label "Color-Names"
  description "perceptible colors denoted by their names"
}
strAtt namedColorAzure [ NamedColor -> "azure" ]
strAtt namedColorPink [ NamedColor -> "pink" ]
strAtt namedColorIvory [ NamedColor -> "ivory" ]
strAtt namedColorWhite [ NamedColor -> "white" ]

sensor StampValue {
	label "Stamp-Value"
	unit "Cent"
}
intAtt Euro045 [ StampValue -> 45 ]
intAtt Euro060 [ StampValue -> 60 ]
intAtt Euro090 [ StampValue -> 90 ]
intAtt Euro145 [ StampValue -> 145 ]
intAtt Euro240 [ StampValue -> 240 ]

sensor PreprintedAddrField {
  label "Preprinted-Addr-Field"
  description "contains a preprinted address field"
}
strAtt withPreprintedAddrField [ PreprintedAddrField -> "T" ]
strAtt withoutPreprintedAddrField [ PreprintedAddrField -> "F" ]

sensor PaperThickness {
  label "Paper-Thickness"
  description "thickness of paper as one criteria of paper quality"
  unit "g/qm"
}
intAtt P70gQuality [ PaperThickness -> 70 ]
intAtt P80gQuality [ PaperThickness -> 80 ]
intAtt P90gQuality [ PaperThickness -> 90 ]

sensor DinANumber {
	label "DinANumbers"
	description "sensor to perceive Din A Format Numbers"
}

intAtt DinA0Number [ DinANumber -> 0 ]
intAtt DinA1Number [ DinANumber -> 1 ]
intAtt DinA2Number [ DinANumber -> 2 ]
intAtt DinA3Number [ DinANumber -> 3 ]
intAtt DinA4Number [ DinANumber -> 4 ]
intAtt DinA5Number [ DinANumber -> 5 ]
intAtt DinA6Number [ DinANumber -> 6 ]
intAtt DinA7Number [ DinANumber -> 7 ]
intAtt DinA8Number [ DinANumber -> 8 ]
intAtt DinA9Number [ DinANumber -> 9 ]
intAtt DinA10Number [ DinANumber -> 10 ]

sensor DinCNumber {
	label "DinCNumbers"
	description "sensor to perceive Din C Format Numbers"
}

floatAtt DinC0Number [ DinCNumber -> 0.0 ]
floatAtt DinC1Number [ DinCNumber -> 1.0 ]
floatAtt DinC2Number [ DinCNumber -> 2.0 ]
floatAtt DinC3Number [ DinCNumber -> 3.0 ]
floatAtt DinC4Number [ DinCNumber -> 4.0 ]
floatAtt DinC5Number [ DinCNumber -> 5.0 ]
floatAtt DinC56Number [ DinCNumber -> 5.6 ]
floatAtt DinC6Number [ DinCNumber -> 6.0 ]
floatAtt DinC7Number [ DinCNumber -> 7.0 ]
floatAtt DinC8Number [ DinCNumber -> 8.0 ]
floatAtt DinC9Number [ DinCNumber -> 9.0 ]
floatAtt DinC10Number [ DinCNumber -> 10.0 ]

sensor DinFormatHeight {
  label "DinFormatHeight"
  description 
  	"DIN form based heights of paper sizes:
	 DIN-A as portrait (height > width )
     DIN-C as landscape (width > height)"
  unit "cm"
  }
floatAtt DinA0Height [ DinFormatHeight -> 118.9 ]
floatAtt DinA1Height [ DinFormatHeight -> 84.1 ]
floatAtt DinA2Height [ DinFormatHeight -> 59.4 ]
floatAtt DinA3Height [ DinFormatHeight -> 42.0 ]
floatAtt DinA4Height [ DinFormatHeight -> 29.7 ]
floatAtt DinA5Height [ DinFormatHeight -> 21.0 ]
floatAtt DinA6Height [ DinFormatHeight -> 14.8 ]
floatAtt DinA7Height [ DinFormatHeight -> 10.5 ]
floatAtt DinA8Height [ DinFormatHeight -> 7.4 ]
floatAtt DinA9Height [ DinFormatHeight -> 5.2 ]
floatAtt DinA10Height [ DinFormatHeight -> 3.7 ]

floatAtt DinC0Height [ DinFormatHeight -> 91.7 ]
floatAtt DinC1Height [ DinFormatHeight -> 64.8 ]
floatAtt DinC2Height [ DinFormatHeight -> 45.8 ]
floatAtt DinC3Height [ DinFormatHeight -> 32.4 ]
floatAtt DinC4Height [ DinFormatHeight -> 22.9 ]
floatAtt DinC5Height [ DinFormatHeight -> 16.2 ]
floatAtt DinC6Height [ DinFormatHeight -> 11.4 ]
floatAtt DinC7Height [ DinFormatHeight -> 8.1 ]
floatAtt DinC8Height [ DinFormatHeight -> 5.7 ]
floatAtt DinC9Height [ DinFormatHeight -> 4.0 ]
floatAtt DinC10Height [ DinFormatHeight -> 2.8 ]

sensor DinFormatWidth {
  label "DinFormatWidth"
  description 
  	"DIN form based widths of paper sizes:
	 DIN-A as portrait (height > width )
     DIN-C as landscape (width > height)"
  unit "cm"
  }
floatAtt DinA0Width [ DinFormatWidth -> 84.1 ]
floatAtt DinA1Width [ DinFormatWidth -> 59.4 ]
floatAtt DinA2Width [ DinFormatWidth -> 42.0 ]
floatAtt DinA3Width [ DinFormatWidth -> 29.7 ]
floatAtt DinA4Width [ DinFormatWidth -> 21.0 ]
floatAtt DinA5Width [ DinFormatWidth -> 14.8 ]
floatAtt DinA6Width [ DinFormatWidth -> 10.5 ]
floatAtt DinA7Width [ DinFormatWidth -> 7.4 ]
floatAtt DinA8Width [ DinFormatWidth -> 5.2 ]
floatAtt DinA9Width [ DinFormatWidth -> 3.7 ]
floatAtt DinA10Width [ DinFormatWidth -> 2.6 ]

floatAtt DinC0Width [ DinFormatWidth -> 129.7 ]
floatAtt DinC1Width [ DinFormatWidth -> 91.7 ]
floatAtt DinC2Width [ DinFormatWidth -> 64.8 ]
floatAtt DinC3Width [ DinFormatWidth -> 45.8 ]
floatAtt DinC4Width [ DinFormatWidth -> 32.4 ]
floatAtt DinC5Width [ DinFormatWidth -> 22.9 ]
floatAtt DinC6Width [ DinFormatWidth -> 16.2 ]
floatAtt DinC7Width [ DinFormatWidth -> 11.4 ]
floatAtt DinC8Width [ DinFormatWidth -> 8.1 ]
floatAtt DinC9Width [ DinFormatWidth -> 5.7 ]
floatAtt DinC10Width [ DinFormatWidth -> 4.0 ]

concept DinA6Letter {
  label "DIN-A6-Values"
  description "DIN A6 constitutive values"
  [
  	[int]> DinA6Number
  	and [float]> DinA6Height
  	and [float]> DinA6Width
  ]
}

concept DinA5Letter {
  label "DIN-A5-Values"
  description "DIN A5 constitutive values"
  [
  	[int]> DinA5Number
  	and [float]> DinA5Height
  	and [float]> DinA5Width
  ]
}

concept DinA4Letter {
  label "DIN-A4-Values"
  description "DIN A4 constitutive values"
  [
  	[int]> DinA4Number
  	and [float]> DinA4Height
  	and [float]> DinA4Width
  ]
}

concept DinA3Letter {
  label "DIN-A3-Values"
  description "DIN A3 constitutive values"
  [
  	[int]> DinA3Number
  	and [float]> DinA3Height
  	and [float]> DinA3Width
  ]
}

concept DinA2Letter {
  label "DIN-A2-Values"
  description "DIN A2 constitutive values"
  [
  	[int]> DinA2Number
  	and [float]> DinA2Height
  	and [float]> DinA2Width
  ]
}

concept DinA6CardConveyable {
  label "Conveyable-DIN-A6-Writing-Card"
  description "constitutive values for a conveyable DIN A6 writing card"
  [
  	[str]> withPreprintedAddrField
  	and [int]> DinA6Number
  	and [float]> DinA6Height
  	and [float]> DinA6Width
  ]
}

concept DinA6CardUnconveyable {
  label "Conveyable-DIN-A6-Writing-Card"
  description "constitutive values for an unconveyable DIN A6 writing card"
  [
  	[str]> withPreprintedAddrField
  	and [int]> DinA6Number
  	and [float]> DinA6Height
  	and [float]> DinA6Width
  ]
}

concept DinC4Envelope {
  label "DIN-C4-Envelope"
  description "DIN C4 constitutive values"
  [
  	[float]> DinC4Number
  	and [float]> DinC4Height
  	and [float]> DinC4Width
  ]
}

concept DinC5Envelope {
  label "DIN-C5-Envelope"
  description "DIN C5 constitutive values"
  [
  	[float]> DinC5Number
  	and [float]> DinC5Height
  	and [float]> DinC5Width
  ]
}

concept DinC56Envelope {
  label "DIN-C5,6-Envelope"
  description "DIN C5,6 constitutive values"
  [
  	[float]> DinC56Number
  	and [float]> DinC6Height
  	and [float]> DinC5Width
  ]
}

concept DinC6Envelope {
  label "DIN-C6-Envelope"
  description "DIN C6 constitutive values"
  [
  	[float]> DinC6Number
  	and [float]> DinC6Height
  	and [float]> DinC6Width
  ]
}


purpose informRemoteAddressee {
	root true
 	label "informRemoteAdressee"
	description "something to inform a remote addressees"
}
	
purpose fileMessages {
	root false
	label "writeMessagesOn"
	description "something to write a messages on it"
}

purpose compressMessageMedia {
	label "compressMessageMedia"
	description "something usable to compress the media containing the messages"
}

purpose protectMessage {
	label "protectMessageMedia"
	description "something usable to protect the media containing the messages"
}
	
purpose enableMessageTransport {
	label "enableMessageTransport"
	description "something usable to enable the transport of a protected message"
}


variant CompressedLetter {
	label "Compressed-Letter"
	description "a letter containing a compressed media"
	sensed by NamedColor as [
		[str]> namedColorAzure
		or [str]> namedColorPink
		or [str]> namedColorIvory
		or [str]> namedColorWhite
    ]

    sensed by DinCNumber as [
		[float]> DinC4Number 
		or [float]> DinC5Number
		or [float]> DinC56Number
		or [float]> DinC6Number    	
    ]
}


variant UncompressedLetter {
	label "Uncompressed-Letter"
	description "a letter containing an uncompressed media"
	sensed by NamedColor as [
		[str]> namedColorAzure
		or [str]> namedColorPink
		or [str]> namedColorIvory
		or [str]> namedColorWhite
    ]
    sensed by DinCNumber as [
		[float]> DinC4Number
		or [float]> DinC5Number
		or [float]> DinC6Number    	
    ]
}

variant Postcard {
	label "Postcard"
	description "standard postcard"
	sensed by NamedColor as [
		[str]> namedColorIvory
		or [str]> namedColorWhite
    ]
}

variant Envelope {
	label "Envelope"
	description "an envelope for protecting a written message"
	sensed by NamedColor as [
		[str]> namedColorAzure
		or [str]> namedColorPink
		or [str]> namedColorIvory
		or [str]> namedColorWhite
    ]
    classified as [ 
    	[concept]> DinC4Envelope
    	or [concept]> DinC5Envelope
    	or [concept]> DinC56Envelope
    	or [concept]> DinC6Envelope
    ]
}

variant WritingPaper {
	label "WritingPaper"
	description "a page of letter paper"
	sensed by NamedColor as [
		[str]> namedColorAzure
		or [str]> namedColorPink
		or [str]> namedColorIvory
		or [str]> namedColorWhite
    ]
    sensed by PaperThickness as [
    	 [int]> P70gQuality
    	or [int]> P80gQuality
    	or [int]> P90gQuality
    ]
    classified as [
    	[concept]> DinA6Letter
    	or [concept]> DinA5Letter
    	or [concept]> DinA4Letter
    	or [concept]> DinA3Letter
    	or [concept]> DinA2Letter
    ]
}

variant WritingCard {
	label "WritingCard"
	description "a card for writing a message"
	sensed by NamedColor as [
		[str]> namedColorAzure
		or [str]> namedColorPink
		or [str]> namedColorIvory
		or [str]> namedColorWhite
    ]
    classified as [
    	[concept]> DinA6CardConveyable
    	or [concept]> DinA6CardUnconveyable
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
	sensed by StampValue as [
		[int]> Euro045
		or [int]> Euro060
		or [int]> Euro090
		or [int]> Euro145
		or [int]> Euro240
	]
}

purpose.system informRemoteAddressee isFulfilledBy {
	CompressedLetter UncompressedLetter Postcard
}

purpose.system compressMessageMedia isFulfilledBy {
	FoldMethod12 FoldMethod13 FoldMethod14
}

purpose.system protectMessage isFulfilledBy { Envelope }

purpose.system fileMessages isFulfilledBy { WritingPaper WritingCard }

purpose.system enableMessageTransport isFulfilledBy { Stamp }

purpose.variant CompressedLetter isConstitutedBy { 
	compressMessageMedia protectMessage fileMessages enableMessageTransport
}

purpose.variant UncompressedLetter isConstitutedBy { 
	protectMessage fileMessages enableMessageTransport
}

purpose.variant Postcard isConstitutedBy { fileMessages enableMessageTransport }



event.var writingCardAsPostcard [
	label "the-writing-card-as-a-postcard"
	description "using the writing card for filing the message of a postcard"
	context < Postcard fileMessages >
	fact [variant]> WritingCard
]

event.str preparedAddressAreaOnWritingCardAsPostcard [
	label "preprinted-address-area-on-writing-card-as-postcard"
	description "writing card with preprinted address area being used as postcard"
	context < Postcard fileMessages WritingCard >
	fact [str]> withPreprintedAddrField
]

event.int stamp045AsPostcardEnabler [
	label "045-cent-stamp-as-postcard-transport-enabler"
	description "45 cent stamp for enabling the transport of a postcard"
	context < Postcard fileMessages WritingCard >
	fact [int]> Euro045 
]

event.sensor PostcardColor [
	label "Postcard-Color"
	context < Postcard >
	fact [sensor]> NamedColor
]

event.sensor WritingCardPostcardColor [
	label "Writing-Card-Color-Used-As-Postcard"
	context < Postcard fileMessages WritingCard >
	fact [sensor]> NamedColor
]


event.str uncompressedLetterWritingCardWithoutAddressField [
	label "an-uncompressed-letter-with-a-writing-card-without-addr-field"
    description "no preprinted address area on a writing card used as uncompressed letter"
    context < UncompressedLetter fileMessages WritingCard >
    NOT
    fact [str]> withPreprintedAddrField
]

event.concept uncompressedLetterInDinC6Envelope [
	label "uncompressed-letter-in-a-DIN-C6-envelope"
	context < UncompressedLetter protectMessage Envelope >
	fact [concept]> DinC6Envelope
]

event.sensor uncompressedLetterEnvelopDinSize [
	label "uncompressed-letter-envelope-din-size"
	context < UncompressedLetter protectMessage Envelope >
	fact [sensor]> DinCNumber
]

event.sensor uncompressedLetterEnvelopColor [
	label "uncompressed-letter-envelope-color"
	context < UncompressedLetter protectMessage Envelope >
	fact [sensor]> NamedColor
]


event.sensor uncompressedLetterPaperDinSize [
	label "uncompressed-letter-paper-din-size"
	context < UncompressedLetter fileMessages WritingPaper >
	fact [sensor]> DinANumber
]


event.sensor uncompressedLetterDinSize [
	label "uncompressed-letter-din-size"
	description "the size of an uncompressed letter itself"
	context < UncompressedLetter >
	fact [sensor]> DinCNumber
]

event.sensor uncompressedLetterColor [
	label "uncompressed-letter-color"
	description "the color of an uncompressed letter itself"
	context < UncompressedLetter >
	fact [sensor]> NamedColor
]

event.float UncompressedDinC6Letter [
	label "uncompressed-Din-C6-Letter"
	description "a typical DIN C6 Letter denoted by its Din-Number"
	context < UncompressedLetter >
	fact [float]> DinC6Number
]

/* Note: Lettersize 5.6 for uncompressed letter not defined */
event.concept UncompressedDinC5Letter [
	label "uncompressed-Din-C5-Letter"
	description "a typical DIN C5 Letter denoted by the concept of all DIN-C6-features of its envelope"
	context < UncompressedLetter  protectMessage Envelope>
	fact [concept]> DinC6Envelope
]

event.float UncompressedDinC4Letter [
	label "uncompressed-Din-C4-Letter"
	description "a typical DIN C4 Letter denoted by the Din-Number of the envelope"
	context < UncompressedLetter protectMessage Envelope>
	fact [float]> DinC4Number
]

event.int UncompressedLetterStamp060Cent [
	label "uncompressed-letter-stamp-with-value-60"
	context < UncompressedLetter enableMessageTransport Stamp >
	fact [int]> Euro060
]

event.int UncompressedLetterStamp145Cent [
	label "uncompressed-letter-stamp-with-value-60"
	context < UncompressedLetter enableMessageTransport Stamp >
	fact [int]> Euro145
]


event.var compressedLetterWithPaper [
	label "the-compressed-letter-written-on-paper"
	context < CompressedLetter fileMessages >
	fact [variant]> WritingPaper
]

event.sensor compressedLetterPaperSize [
	label "compressed-letter-paper-size"
	description "size of the paper in the context of a compressed letter"
	context < CompressedLetter fileMessages WritingPaper >
	fact [sensor]> DinANumber
]

event.sensor compressedLetterEnvelopeSize [
	label "compressed-letter-envelope-size"
	description "size of the envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope >
	fact [sensor]> DinCNumber
]

event.int compressedLetterWithDinA2Paper [
	label "compressed-letter-with-DIN-A2-paper"
	description "DIN-A2 paper in the context of a compressed letter"
	context < CompressedLetter fileMessages WritingPaper >
	fact [int]> DinA2Number
]

event.int compressedLetterWithDinA3Paper [
	label "compressed-letter-with-DIN-A3-paper"
	description "DIN-A3 paper in the context of a compressed letter"
	context < CompressedLetter fileMessages WritingPaper >
	fact [int]> DinA3Number
]

event.int compressedLetterWithDinA4Paper [
	label "compressed-letter-with-DIN-A4-paper"
	description "DIN-A4 paper in the context of a compressed letter"
	context < CompressedLetter fileMessages WritingPaper >
	fact [int]> DinA4Number
]

event.int compressedLetterWithDinA5Paper [
	label "compressed-letter-with-DIN-A5-paper"
	description "DIN-A5 paper in the context of a compressed letter"
	context < CompressedLetter fileMessages WritingPaper >
	fact [int]> DinA5Number
]

event.float compressedLetterWithDinC4Envelope [
	label "compressed-letter-with-DIN-C4-envelope"
	description "DIN-C4 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope  >
	fact [float]> DinC4Number
]

event.float compressedLetterWithDinC5Envelope [
	label "compressed-letter-with-DIN-C5-envelope"
	description "DIN-C5 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope >
	fact [float]> DinC5Number
]

event.float compressedLetterWithDinC56Envelope [
	label "compressed-letter-with-DIN-C56-envelope"
	description "DIN-C56 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope >
	fact [float]> DinC56Number
]

event.float compressedLetterWithoutDinC56Envelope [
	label "compressed-letter-without-DIN-C56-envelope"
	description "not using a DIN-C56 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope >
	NOT
	fact [float]> DinC56Number
]

event.float compressedLetterWithDinC6Envelope [
	label "compressed-letter-with-DIN-C6-envelope"
	description "DIN-C6 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope >
	fact [float]> DinC6Number
]

event.float compressedLetterWithoutDinC6Envelope [
	label "compressed-letter-without-DIN-C6-envelope"
	description "not using a DIN-C6 envelope in the context of a compressed letter"
	context < CompressedLetter protectMessage Envelope >
	fact [float]> DinC6Number
]
 event.var compressedLetterFoldHalfInSize [
	label "compressed-letter-using-the-half-in-size-folding-method"
	context < CompressedLetter compressMessageMedia >
	fact [variant]> FoldMethod12
]

 event.var compressedLetterFoldThirdInSize [
	label "compressed-letter-using-the-third-in-size-folding-method"
	context < CompressedLetter compressMessageMedia >
	fact [variant]> FoldMethod13
]

 event.var compressedLetterFoldQuarterInSize [
	label "compressed-letter-using-the-quarter-in-size-folding-method"
	context < CompressedLetter compressMessageMedia >
	fact [variant]> FoldMethod14
]

event.sensor compressedLetterDinSize [
	label "compressed-letter-din-size"
	description "the size of the compressed letter itself"
	context < CompressedLetter >
	fact [sensor]> DinCNumber
]

event.sensor compressedLetterColor [
	label "compressed-letter-color"
	description "the color of the compressed letter itself"
	context < CompressedLetter >
	fact [sensor]> NamedColor
]

event.sensor compressedLetterEnvelopeColor [
	label "compressed-letter-envelope-color"
	description "the color of the envelope of a compressed"
	context < CompressedLetter protectMessage Envelope>
	fact [sensor]> NamedColor
]

event.float compressedDinC6Letter [
	label "compressed-Din-C6-Letter"
	description "a typical compressed DIN C6 Letter denoted by its Din-Number"
	context < CompressedLetter >
	fact [float]> DinC6Number
]

event.float compressedDinC56Letter [
	label "compressed-Din-C56-Letter"
	description "a typical compressed DIN C56 Letter denoted by its Din-Number"
	context < CompressedLetter >
	fact [float]> DinC56Number
]

event.float compressedDinC5Letter [
	label "compressed-Din-C5-Letter"
	description "a typical compressed DIN C5 Letter denoted by its Din-Number"
	context < CompressedLetter >
	fact [float]> DinC5Number
]

event.float compressedDinC4Letter [
	label "compressed-Din-C4-Letter"
	description "a typical compressed DIN C4 Letter denoted by its Din-Number"
	context < CompressedLetter >
	fact [float]> DinC4Number
]

event.int compressedLetterStamp060Cent [
	label "uncompressed-letter-stamp-with-value-60"
	context < CompressedLetter enableMessageTransport Stamp >
	fact [int]> Euro060
]

event.int compressedLetterStamp145Cent [
	label "uncompressed-letter-stamp-with-value-145"
	context < CompressedLetter enableMessageTransport Stamp >
	fact [int]> Euro145
]

logic.enforcer [variant]> Postcard -> writingCardAsPostcard
logic.enforcer [variant]> Postcard -> preparedAddressAreaOnWritingCardAsPostcard
logic.enforcer [variant]> Postcard -> stamp045AsPostcardEnabler

relation.equal PostcardColor == WritingCardPostcardColor

logic.enforcer [variant]> UncompressedLetter -> uncompressedLetterWritingCardWithoutAddressField
logic.enforcer [variant]> UncompressedLetter -> uncompressedLetterInDinC6Envelope
logic.rule UncompressedDinC6Letter -> UncompressedLetterStamp060Cent
logic.rule UncompressedDinC5Letter -> UncompressedLetterStamp145Cent
logic.rule UncompressedDinC4Letter -> UncompressedLetterStamp145Cent

relation.equal uncompressedLetterDinSize == uncompressedLetterEnvelopDinSize
relation.less-or-equal uncompressedLetterEnvelopDinSize <= uncompressedLetterPaperDinSize
relation.equal uncompressedLetterColor == uncompressedLetterEnvelopColor

/* RULE-3.A */
logic.enforcer [variant]> CompressedLetter -> compressedLetterWithPaper

/* A2 paper only in C4 envelope by using the method 'quarter in size' */
/* RULE-3.C.a */
logic.rule compressedLetterWithDinA2Paper -> compressedLetterFoldQuarterInSize
/* RULE-3.C.b */
logic.rule compressedLetterWithDinA2Paper -> compressedLetterWithDinC4Envelope

/* A3 paper only in C4 or C5 envelope, hence not in C6 or C56 ... */
/* RULE-3.D.c */
logic.rule compressedLetterWithDinA3Paper -> compressedLetterWithoutDinC56Envelope
/* RULE-3.D.d */
logic.rule compressedLetterWithDinA3Paper -> compressedLetterWithDinC6Envelope
/* RULE-3.D.a */
logic.rule compressedLetterWithDinA3Paper and compressedLetterWithDinC4Envelope
  -> compressedLetterFoldHalfInSize
/* RULE-3.D.b */ 
logic.rule compressedLetterWithDinA3Paper and compressedLetterWithDinC5Envelope 
  -> compressedLetterFoldQuarterInSize

/* A4 paper only in C5, C56, C6 envelope by ... */
/* RULE-3.E.a */ 
logic.rule compressedLetterWithDinA4Paper and compressedLetterWithDinC5Envelope 
  -> compressedLetterFoldHalfInSize
/* RULE-3.E.b */ 
logic.rule compressedLetterWithDinA4Paper and compressedLetterWithDinC56Envelope 
  -> compressedLetterFoldThirdInSize  
/* RULE-3.E.c */
logic.rule compressedLetterWithDinA4Paper and compressedLetterWithDinC6Envelope 
  -> compressedLetterFoldQuarterInSize
/* RULE-3.F.a */  
logic.rule compressedLetterFoldThirdInSize -> compressedLetterWithDinC56Envelope
/* RULE-3.F.b */  
logic.rule compressedLetterFoldThirdInSize -> compressedLetterWithDinA4Paper

/* A5 paper only in C6 envelope by ... */ 
/* RULE-3.G */ 
logic.rule compressedLetterWithDinA5Paper -> compressedLetterFoldHalfInSize
 
/* RULE-3.I.a */ 
logic.rule compressedDinC6Letter ->   compressedLetterStamp060Cent
/* RULE-3.I.b */ 
logic.rule compressedDinC56Letter -> compressedLetterStamp060Cent
/* RULE-3.I.c */ 
logic.rule UncompressedDinC5Letter -> UncompressedLetterStamp145Cent
/* RULE-3.I.d */ 
logic.rule UncompressedDinC4Letter -> UncompressedLetterStamp145Cent



/* RULE 3.B */  
relation.less compressedLetterEnvelopeSize < compressedLetterPaperSize
/* RULE-3.H.a */
relation.equal compressedLetterEnvelopeSize == compressedLetterDinSize
/* RULE-3.H.b */
relation.equal compressedLetterEnvelopeColor == compressedLetterColor