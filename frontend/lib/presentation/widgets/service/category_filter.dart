import 'package:flutter/material.dart';

class CategoryFilter extends StatelessWidget {
  final String selectedCategory;
  final Function(String) onCategorySelected;
  final List<String> categories;

  const CategoryFilter({
    super.key,
    required this.selectedCategory,
    required this.onCategorySelected,
    this.categories = const [
      'ALL',
      'NURSING',
      'ELDERLY',
      'PHYSIOTHERAPY',
      'CHILD_CARE',
      'AMBULANCE',
    ],
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 50,
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: categories.length,
        itemBuilder: (context, index) {
          final category = categories[index];
          final isSelected = selectedCategory == category;
          
          return Padding(
            padding: const EdgeInsets.only(right: 8),
            child: FilterChip(
              label: Text(_getCategoryLabel(category)),
              selected: isSelected,
              onSelected: (selected) {
                if (selected) {
                  onCategorySelected(category);
                }
              },
              selectedColor: Theme.of(context).primaryColor.withOpacity(0.2),
              checkmarkColor: Theme.of(context).primaryColor,
              labelStyle: TextStyle(
                color: isSelected 
                    ? Theme.of(context).primaryColor 
                    : Colors.grey[600],
                fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
              ),
              backgroundColor: Colors.grey[100],
              side: BorderSide(
                color: isSelected 
                    ? Theme.of(context).primaryColor 
                    : Colors.grey[300]!,
                width: isSelected ? 2 : 1,
              ),
            ),
          );
        },
      ),
    );
  }

  String _getCategoryLabel(String category) {
    switch (category) {
      case 'ALL':
        return 'All';
      case 'NURSING':
        return 'Nursing';
      case 'ELDERLY':
        return 'Elderly Care';
      case 'PHYSIOTHERAPY':
        return 'Physiotherapy';
      case 'CHILD_CARE':
        return 'Child Care';
      case 'AMBULANCE':
        return 'Ambulance';
      default:
        return category;
    }
  }
}
