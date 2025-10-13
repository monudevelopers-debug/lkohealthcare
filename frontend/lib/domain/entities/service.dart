class Service {
  final String? id;
  final String? name;
  final String? description;
  final String? category;
  final String? categoryId;
  final double? price;
  final int? duration;
  final bool? isActive;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const Service({
    this.id,
    this.name,
    this.description,
    this.category,
    this.categoryId,
    this.price,
    this.duration,
    this.isActive,
    this.createdAt,
    this.updatedAt,
  });

  factory Service.fromJson(Map<String, dynamic> json) {
    return Service(
      id: json['id']?.toString(),
      name: json['name'],
      description: json['description'],
      category: json['category'],
      categoryId: json['categoryId']?.toString(),
      price: json['price']?.toDouble(),
      duration: json['duration'],
      isActive: json['isActive'],
      createdAt: json['createdAt'] != null 
          ? DateTime.tryParse(json['createdAt']) 
          : null,
      updatedAt: json['updatedAt'] != null 
          ? DateTime.tryParse(json['updatedAt']) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'category': category,
      'categoryId': categoryId,
      'price': price,
      'duration': duration,
      'isActive': isActive,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  Service copyWith({
    String? id,
    String? name,
    String? description,
    String? category,
    String? categoryId,
    double? price,
    int? duration,
    bool? isActive,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return Service(
      id: id ?? this.id,
      name: name ?? this.name,
      description: description ?? this.description,
      category: category ?? this.category,
      categoryId: categoryId ?? this.categoryId,
      price: price ?? this.price,
      duration: duration ?? this.duration,
      isActive: isActive ?? this.isActive,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  @override
  String toString() {
    return 'Service(id: $id, name: $name, category: $category, price: $price, duration: $duration)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is Service && other.id == id;
  }

  @override
  int get hashCode => id.hashCode;
}
